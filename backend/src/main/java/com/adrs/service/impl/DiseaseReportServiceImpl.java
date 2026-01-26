package com.adrs.service.impl;

import com.adrs.dto.AnimalTypeDTO;
import com.adrs.dto.DiseaseDTO;
import com.adrs.dto.DiseaseMapDTO;
import com.adrs.dto.DiseaseReportRequestDTO;
import com.adrs.dto.DiseaseReportResponseDTO;
import com.adrs.exception.ConfigurationNotFoundException;
import com.adrs.model.*;
import com.adrs.repository.*;
import com.adrs.service.DiseaseReportService;
import com.adrs.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of DiseaseReportService.
 * Handles all business logic for disease report management.
 */
@Service
@Transactional
public class DiseaseReportServiceImpl implements DiseaseReportService {

    private static final Logger logger = LoggerFactory.getLogger(DiseaseReportServiceImpl.class);
    private static final String DISEASE_IMAGES_DIR = "disease-images";

    private final DiseaseReportRepository diseaseReportRepository;
    private final FarmRepository farmRepository;
    private final AnimalTypeRepository animalTypeRepository;
    private final DiseaseRepository diseaseRepository;
    private final FileStorageService fileStorageService;

    public DiseaseReportServiceImpl(
            DiseaseReportRepository diseaseReportRepository,
            FarmRepository farmRepository,
            AnimalTypeRepository animalTypeRepository,
            DiseaseRepository diseaseRepository,
            FileStorageService fileStorageService) {
        this.diseaseReportRepository = diseaseReportRepository;
        this.farmRepository = farmRepository;
        this.animalTypeRepository = animalTypeRepository;
        this.diseaseRepository = diseaseRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public DiseaseReportResponseDTO createReport(DiseaseReportRequestDTO request, MultipartFile image, User reporter) {
        logger.info("Creating disease report for farm: {} by user: {}", request.getFarmId(), reporter.getUsername());

        // Validate and fetch related entities
        Farm farm = farmRepository.findById(request.getFarmId())
                .orElseThrow(() -> new ConfigurationNotFoundException("Farm", request.getFarmId()));

        AnimalType animalType = animalTypeRepository.findById(request.getAnimalTypeId())
                .orElseThrow(() -> new ConfigurationNotFoundException("AnimalType", request.getAnimalTypeId()));

        // Handle disease - either existing or create new "Other" disease
        Disease disease;
        if (Boolean.TRUE.equals(request.getIsOtherDisease())) {
            disease = createOtherDisease(request, animalType, reporter);
        } else {
            if (request.getDiseaseId() == null) {
                throw new IllegalArgumentException("Disease ID is required when not reporting an 'Other' disease");
            }
            disease = diseaseRepository.findById(request.getDiseaseId())
                    .orElseThrow(() -> new ConfigurationNotFoundException("Disease", request.getDiseaseId()));
        }

        // Validate affected count does not exceed registered animal count
        if (request.getAffectedCount() != null) {
            Integer registeredCount = farm.getFarmAnimals().stream()
                    .filter(fa -> fa.getAnimalType().getId().equals(animalType.getId()))
                    .findFirst()
                    .map(fa -> fa.getCount())
                    .orElse(0);
            
            if (request.getAffectedCount() > registeredCount) {
                throw new IllegalArgumentException(
                        String.format("Affected count (%d) cannot exceed registered animal count (%d) for %s on this farm",
                                request.getAffectedCount(), registeredCount, animalType.getTypeName()));
            }
        }

        // Create the disease report
        DiseaseReport report = new DiseaseReport();
        report.setFarm(farm);
        report.setAnimalType(animalType);
        report.setDisease(disease);
        report.setReportedBy(reporter);
        report.setReportDate(request.getReportDate());
        report.setAffectedCount(request.getAffectedCount());
        report.setSymptoms(request.getSymptoms());
        report.setDiagnosis(request.getDiagnosis());
        report.setTreatment(request.getTreatment());
        report.setOutcome(request.getOutcome() != null ? request.getOutcome() : DiseaseReport.Outcome.ONGOING);
        report.setNotes(request.getNotes());
        report.setIsConfirmed(false); // Start as unconfirmed

        // Handle override fields (vet-specific, does not affect admin data)
        report.setOverrideDiseaseName(request.getOverrideDiseaseName());
        if (request.getOverrideSeverity() != null && !request.getOverrideSeverity().isEmpty()) {
            report.setOverrideSeverity(Disease.Severity.valueOf(request.getOverrideSeverity()));
        }
        report.setOverrideDescription(request.getOverrideDescription());
        report.setOverrideNotifiable(request.getOverrideNotifiable());

        // Handle image upload
        if (image != null && !image.isEmpty()) {
            String imagePath = fileStorageService.storeFile(image, DISEASE_IMAGES_DIR);
            report.setImagePath(imagePath);
            logger.info("Image uploaded for disease report: {}", imagePath);
        }

        DiseaseReport savedReport = diseaseReportRepository.save(report);
        logger.info("Disease report created with ID: {}", savedReport.getId());

        return convertToDTO(savedReport);
    }

    @Override
    @Transactional(readOnly = true)
    public DiseaseReportResponseDTO getReportById(UUID id) {
        logger.debug("Fetching disease report with ID: {}", id);

        DiseaseReport report = diseaseReportRepository.findById(id)
                .orElseThrow(() -> new ConfigurationNotFoundException("DiseaseReport", id));

        return convertToDTO(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiseaseReportResponseDTO> getReportsByVet(User vet) {
        logger.debug("Fetching disease reports for vet: {}", vet.getUsername());

        List<DiseaseReport> reports = diseaseReportRepository.findByReportedByOrderByCreatedAtDesc(vet);
        return reports.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiseaseReportResponseDTO> getReportsByFarm(UUID farmId) {
        logger.debug("Fetching disease reports for farm: {}", farmId);

        List<DiseaseReport> reports = diseaseReportRepository.findByFarmIdOrderByCreatedAtDesc(farmId);
        return reports.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getReportCountByVet(User vet) {
        return diseaseReportRepository.countByReportedBy(vet);
    }

    @Override
    public DiseaseReportResponseDTO updateReport(UUID id, DiseaseReportRequestDTO request, MultipartFile image, User updater) {
        logger.info("Updating disease report: {} by user: {}", id, updater.getUsername());

        DiseaseReport report = diseaseReportRepository.findById(id)
                .orElseThrow(() -> new ConfigurationNotFoundException("DiseaseReport", id));

        // Update fields
        if (request.getAnimalTypeId() != null) {
            AnimalType animalType = animalTypeRepository.findById(request.getAnimalTypeId())
                    .orElseThrow(() -> new ConfigurationNotFoundException("AnimalType", request.getAnimalTypeId()));
            report.setAnimalType(animalType);
        }

        if (request.getDiseaseId() != null) {
            Disease disease = diseaseRepository.findById(request.getDiseaseId())
                    .orElseThrow(() -> new ConfigurationNotFoundException("Disease", request.getDiseaseId()));
            report.setDisease(disease);
        }

        if (request.getReportDate() != null) {
            report.setReportDate(request.getReportDate());
        }
        
        // Validate affected count does not exceed registered animal count
        if (request.getAffectedCount() != null) {
            Farm farm = report.getFarm();
            AnimalType animalType = report.getAnimalType();
            Integer registeredCount = farm.getFarmAnimals().stream()
                    .filter(fa -> fa.getAnimalType().getId().equals(animalType.getId()))
                    .findFirst()
                    .map(fa -> fa.getCount())
                    .orElse(0);
            
            if (request.getAffectedCount() > registeredCount) {
                throw new IllegalArgumentException(
                        String.format("Affected count (%d) cannot exceed registered animal count (%d) for %s on this farm",
                                request.getAffectedCount(), registeredCount, animalType.getTypeName()));
            }
        }
        
        report.setAffectedCount(request.getAffectedCount());
        report.setSymptoms(request.getSymptoms());
        report.setDiagnosis(request.getDiagnosis());
        report.setTreatment(request.getTreatment());
        report.setOutcome(request.getOutcome());
        report.setNotes(request.getNotes());

        // Handle override fields (vet-specific, does not affect admin data)
        report.setOverrideDiseaseName(request.getOverrideDiseaseName());
        if (request.getOverrideSeverity() != null && !request.getOverrideSeverity().isEmpty()) {
            report.setOverrideSeverity(Disease.Severity.valueOf(request.getOverrideSeverity()));
        } else {
            report.setOverrideSeverity(null);
        }
        report.setOverrideDescription(request.getOverrideDescription());
        report.setOverrideNotifiable(request.getOverrideNotifiable());

        // Handle image clear request
        if (Boolean.TRUE.equals(request.getClearImage())) {
            // Delete existing image if present
            if (report.getImagePath() != null) {
                fileStorageService.deleteFile(report.getImagePath());
                report.setImagePath(null);
                logger.info("Existing image cleared for disease report: {}", id);
            }
        }
        // Handle new image upload (only if not cleared and new image provided)
        else if (image != null && !image.isEmpty()) {
            // Delete old image if exists
            if (report.getImagePath() != null) {
                fileStorageService.deleteFile(report.getImagePath());
            }
            String imagePath = fileStorageService.storeFile(image, DISEASE_IMAGES_DIR);
            report.setImagePath(imagePath);
        }

        DiseaseReport updatedReport = diseaseReportRepository.save(report);
        logger.info("Disease report updated: {}", updatedReport.getId());

        return convertToDTO(updatedReport);
    }

    @Override
    public void deleteReport(UUID id, User user) {
        logger.info("Deleting disease report: {} by user: {}", id, user.getUsername());

        DiseaseReport report = diseaseReportRepository.findById(id)
                .orElseThrow(() -> new ConfigurationNotFoundException("DiseaseReport", id));

        // Delete associated image if exists
        if (report.getImagePath() != null) {
            fileStorageService.deleteFile(report.getImagePath());
        }

        diseaseReportRepository.delete(report);
        logger.info("Disease report deleted: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiseaseMapDTO> getReportsForMap(List<UUID> animalTypeIds, List<UUID> diseaseIds, Province province) {
        logger.debug("Fetching disease reports for map, animalTypeIds: {}, diseaseIds: {}, province: {}", animalTypeIds, diseaseIds, province);

        List<DiseaseReport> reports;
        boolean hasAnimalFilter = animalTypeIds != null && !animalTypeIds.isEmpty();
        boolean hasDiseaseFilter = diseaseIds != null && !diseaseIds.isEmpty();

        if (hasAnimalFilter && hasDiseaseFilter) {
            reports = diseaseReportRepository.findAllWithFarmGpsCoordinatesByAnimalTypeIdsAndDiseaseIds(animalTypeIds, diseaseIds);
        } else if (hasAnimalFilter) {
            reports = diseaseReportRepository.findAllWithFarmGpsCoordinatesByAnimalTypeIds(animalTypeIds);
        } else if (hasDiseaseFilter) {
            reports = diseaseReportRepository.findAllWithFarmGpsCoordinatesByDiseaseIds(diseaseIds);
        } else {
            reports = diseaseReportRepository.findAllWithFarmGpsCoordinates();
        }

        // Filter by province if specified
        if (province != null) {
            reports = reports.stream()
                    .filter(r -> r.getFarm().getProvince() == province)
                    .collect(Collectors.toList());
        }

        // Group reports by farm
        Map<UUID, List<DiseaseReport>> reportsByFarm = reports.stream()
                .collect(Collectors.groupingBy(r -> r.getFarm().getId()));

        // Convert to DTOs
        List<DiseaseMapDTO> mapDTOs = new ArrayList<>();
        for (Map.Entry<UUID, List<DiseaseReport>> entry : reportsByFarm.entrySet()) {
            List<DiseaseReport> farmReports = entry.getValue();
            if (farmReports.isEmpty()) continue;

            Farm farm = farmReports.get(0).getFarm();
            DiseaseMapDTO mapDTO = new DiseaseMapDTO();
            mapDTO.setFarmId(farm.getId());
            mapDTO.setFarmName(farm.getFarmName());
            mapDTO.setGpsLatitude(farm.getGpsLatitude());
            mapDTO.setGpsLongitude(farm.getGpsLongitude());
            mapDTO.setAddress(farm.getAddress());
            mapDTO.setOwnerName(farm.getOwnerName());
            
            if (farm.getDistrict() != null) {
                mapDTO.setDistrictDisplayName(farm.getDistrict().getDisplayName());
            }
            if (farm.getProvince() != null) {
                mapDTO.setProvinceDisplayName(farm.getProvince().getDisplayName());
            }

            // Map disease reports
            List<DiseaseMapDTO.DiseaseInfo> diseaseInfos = farmReports.stream()
                    .map(r -> {
                        DiseaseMapDTO.DiseaseInfo info = new DiseaseMapDTO.DiseaseInfo();
                        info.setReportId(r.getId());
                        info.setDiseaseName(r.getDisease().getDiseaseName());
                        info.setDiseaseCode(r.getDisease().getDiseaseCode());
                        info.setSeverity(r.getDisease().getSeverity());
                        info.setIsNotifiable(r.getDisease().getIsNotifiable());
                        
                        // Set effective values (use override if present, otherwise use original)
                        String effectiveName = (r.getOverrideDiseaseName() != null && !r.getOverrideDiseaseName().isEmpty())
                                ? r.getOverrideDiseaseName() : r.getDisease().getDiseaseName();
                        Disease.Severity effectiveSeverity = (r.getOverrideSeverity() != null)
                                ? r.getOverrideSeverity() : r.getDisease().getSeverity();
                        Boolean effectiveNotifiable = (r.getOverrideNotifiable() != null)
                                ? r.getOverrideNotifiable() : r.getDisease().getIsNotifiable();
                        
                        info.setEffectiveDiseaseName(effectiveName);
                        info.setEffectiveSeverity(effectiveSeverity);
                        info.setEffectiveNotifiable(effectiveNotifiable);
                        
                        info.setAnimalTypeId(r.getAnimalType().getId());
                        info.setAnimalTypeName(r.getAnimalType().getTypeName());
                        info.setAffectedCount(r.getAffectedCount());
                        info.setReportDate(r.getReportDate());
                        info.setOutcome(r.getOutcome());
                        info.setReportedByUsername(r.getReportedBy().getUsername());
                        return info;
                    })
                    .collect(Collectors.toList());
            
            mapDTO.setDiseases(diseaseInfos);
            mapDTOs.add(mapDTO);
        }

        logger.debug("Returning {} farm locations with disease reports", mapDTOs.size());
        return mapDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnimalTypeDTO> getAnimalTypesWithReports() {
        logger.debug("Fetching animal types with disease reports");

        List<UUID> animalTypeIds = diseaseReportRepository.findDistinctAnimalTypeIdsWithReports();
        
        if (animalTypeIds.isEmpty()) {
            return Collections.emptyList();
        }

        return animalTypeIds.stream()
                .map(id -> animalTypeRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(at -> {
                    AnimalTypeDTO dto = new AnimalTypeDTO();
                    dto.setId(at.getId());
                    dto.setTypeName(at.getTypeName());
                    dto.setDescription(at.getDescription());
                    dto.setIsActive(at.getIsActive());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiseaseDTO> getDiseasesWithReports(List<UUID> animalTypeIds) {
        logger.debug("Fetching diseases with reports, animalTypeIds: {}", animalTypeIds);

        List<UUID> diseaseIds;
        if (animalTypeIds == null || animalTypeIds.isEmpty()) {
            diseaseIds = diseaseReportRepository.findDistinctDiseaseIdsWithReports();
        } else {
            diseaseIds = diseaseReportRepository.findDistinctDiseaseIdsByAnimalTypeIds(animalTypeIds);
        }
        
        if (diseaseIds.isEmpty()) {
            return Collections.emptyList();
        }

        return diseaseIds.stream()
                .map(id -> diseaseRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(d -> {
                    DiseaseDTO dto = new DiseaseDTO();
                    dto.setId(d.getId());
                    dto.setDiseaseName(d.getDiseaseName());
                    dto.setDiseaseCode(d.getDiseaseCode());
                    dto.setDescription(d.getDescription());
                    dto.setSeverity(d.getSeverity());
                    dto.setIsNotifiable(d.getIsNotifiable());
                    dto.setIsActive(d.getIsActive());
                    dto.setCreatedByVet(d.getCreatedByVet());
                    // Set animal type information (use first animal type for backward compatibility)
                    if (d.getAnimalTypes() != null && !d.getAnimalTypes().isEmpty()) {
                        AnimalType firstType = d.getAnimalTypes().iterator().next();
                        dto.setAnimalTypeId(firstType.getId());
                        dto.setAnimalTypeName(firstType.getTypeName());
                        // Also populate the new lists
                        dto.setAnimalTypeIds(d.getAnimalTypes().stream()
                                .map(AnimalType::getId)
                                .collect(Collectors.toList()));
                        dto.setAnimalTypeNames(d.getAnimalTypes().stream()
                                .map(AnimalType::getTypeName)
                                .collect(Collectors.toList()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiseaseReportResponseDTO> getReportsNotByVet(User vet) {
        logger.debug("Fetching disease reports NOT by vet: {}", vet.getUsername());

        List<DiseaseReport> reports = diseaseReportRepository.findByReportedByNotOrderByCreatedAtDesc(vet);
        return reports.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new disease from "Other" disease report data.
     * This is called when a vet reports a disease that doesn't exist in the system.
     *
     * @param request the disease report request containing new disease data
     * @param animalType the animal type for the disease
     * @param reporter the vet creating the disease
     * @return the newly created Disease entity
     */
    private Disease createOtherDisease(DiseaseReportRequestDTO request, AnimalType animalType, User reporter) {
        logger.info("Creating new disease from 'Other' selection by vet: {}", reporter.getUsername());

        // Validate required fields for new disease
        if (request.getNewDiseaseName() == null || request.getNewDiseaseName().trim().isEmpty()) {
            throw new IllegalArgumentException("Disease name is required when reporting an 'Other' disease");
        }
        if (request.getNewDiseaseSeverity() == null || request.getNewDiseaseSeverity().trim().isEmpty()) {
            throw new IllegalArgumentException("Severity is required when reporting an 'Other' disease");
        }
        if (request.getNewDiseaseIsNotifiable() == null) {
            throw new IllegalArgumentException("Notifiable status is required when reporting an 'Other' disease");
        }

        String diseaseName = request.getNewDiseaseName().trim();
        String diseaseCode = request.getNewDiseaseCode() != null ? request.getNewDiseaseCode().trim() : null;

        // Check if disease with same name already exists (case-insensitive)
        if (diseaseRepository.existsByDiseaseNameIgnoreCase(diseaseName)) {
            throw new IllegalArgumentException(
                    String.format("A disease with the name '%s' already exists. Please select it from the dropdown instead.", diseaseName));
        }

        // Check if disease code already exists (if provided)
        if (diseaseCode != null && !diseaseCode.isEmpty() && diseaseRepository.existsByDiseaseCodeIgnoreCase(diseaseCode)) {
            throw new IllegalArgumentException(
                    String.format("A disease with the code '%s' already exists. Please use a different code.", diseaseCode));
        }

        // Create new disease
        Disease disease = new Disease();
        disease.setDiseaseName(diseaseName);
        disease.setDiseaseCode(diseaseCode != null && !diseaseCode.isEmpty() ? diseaseCode : null);
        disease.setDescription(request.getNewDiseaseDescription());
        disease.setSeverity(Disease.Severity.valueOf(request.getNewDiseaseSeverity()));
        disease.setIsNotifiable(request.getNewDiseaseIsNotifiable());
        disease.getAnimalTypes().add(animalType);
        disease.setIsActive(true);
        disease.setCreatedByVet(true);
        disease.setCreatedBy(reporter);

        // Set symptoms from the report if provided
        disease.setSymptoms(request.getSymptoms());
        disease.setTreatment(request.getTreatment());

        Disease savedDisease = diseaseRepository.save(disease);
        logger.info("New disease '{}' created by vet with ID: {}", savedDisease.getDiseaseName(), savedDisease.getId());

        return savedDisease;
    }

    /**
     * Convert DiseaseReport entity to response DTO.
     */
    private DiseaseReportResponseDTO convertToDTO(DiseaseReport report) {
        DiseaseReportResponseDTO dto = new DiseaseReportResponseDTO();

        dto.setId(report.getId());

        // Farm info
        if (report.getFarm() != null) {
            dto.setFarmId(report.getFarm().getId());
            dto.setFarmName(report.getFarm().getFarmName());
            dto.setFarmAddress(report.getFarm().getAddress());
            // Farm location
            if (report.getFarm().getDistrict() != null) {
                dto.setFarmDistrict(report.getFarm().getDistrict().name());
                dto.setFarmDistrictDisplayName(report.getFarm().getDistrict().getDisplayName());
            }
            if (report.getFarm().getProvince() != null) {
                dto.setFarmProvince(report.getFarm().getProvince().name());
                dto.setFarmProvinceDisplayName(report.getFarm().getProvince().getDisplayName());
            }
        }

        // Animal type info
        if (report.getAnimalType() != null) {
            dto.setAnimalTypeId(report.getAnimalType().getId());
            dto.setAnimalTypeName(report.getAnimalType().getTypeName());
        }
        dto.setAffectedCount(report.getAffectedCount());

        // Disease info (admin values)
        if (report.getDisease() != null) {
            dto.setDiseaseId(report.getDisease().getId());
            dto.setDiseaseName(report.getDisease().getDiseaseName());
            dto.setDiseaseCode(report.getDisease().getDiseaseCode());
            dto.setSeverity(report.getDisease().getSeverity());
            dto.setIsNotifiable(report.getDisease().getIsNotifiable());
            dto.setDiseaseDescription(report.getDisease().getDescription());
        }

        // Override fields (vet-specific)
        dto.setOverrideDiseaseName(report.getOverrideDiseaseName());
        dto.setOverrideSeverity(report.getOverrideSeverity());
        dto.setOverrideDescription(report.getOverrideDescription());
        dto.setOverrideNotifiable(report.getOverrideNotifiable());

        // Effective values (use override if set, otherwise use admin value)
        if (report.getDisease() != null) {
            dto.setEffectiveDiseaseName(
                report.getOverrideDiseaseName() != null && !report.getOverrideDiseaseName().isEmpty()
                    ? report.getOverrideDiseaseName()
                    : report.getDisease().getDiseaseName());
            dto.setEffectiveSeverity(
                report.getOverrideSeverity() != null
                    ? report.getOverrideSeverity()
                    : report.getDisease().getSeverity());
            dto.setEffectiveDescription(
                report.getOverrideDescription() != null && !report.getOverrideDescription().isEmpty()
                    ? report.getOverrideDescription()
                    : report.getDisease().getDescription());
            dto.setEffectiveNotifiable(
                report.getOverrideNotifiable() != null
                    ? report.getOverrideNotifiable()
                    : report.getDisease().getIsNotifiable());
        }

        // Report details
        dto.setReportDate(report.getReportDate());
        dto.setSymptoms(report.getSymptoms());
        dto.setDiagnosis(report.getDiagnosis());
        dto.setTreatment(report.getTreatment());
        dto.setOutcome(report.getOutcome());
        dto.setNotes(report.getNotes());

        // Image
        dto.setImagePath(report.getImagePath());
        if (report.getImagePath() != null) {
            dto.setImageUrl(fileStorageService.getFileUrl(report.getImagePath()));
        }

        // Status
        dto.setIsConfirmed(report.getIsConfirmed());
        if (report.getConfirmedBy() != null) {
            dto.setConfirmedByUsername(report.getConfirmedBy().getUsername());
        }
        dto.setConfirmedAt(report.getConfirmedAt());

        // Audit info
        if (report.getReportedBy() != null) {
            dto.setReportedByUsername(report.getReportedBy().getUsername());
        }
        dto.setCreatedAt(report.getCreatedAt());
        dto.setUpdatedAt(report.getUpdatedAt());

        return dto;
    }
}
