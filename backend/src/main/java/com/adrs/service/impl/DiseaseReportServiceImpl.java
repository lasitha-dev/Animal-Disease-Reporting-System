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

        Disease disease = diseaseRepository.findById(request.getDiseaseId())
                .orElseThrow(() -> new ConfigurationNotFoundException("Disease", request.getDiseaseId()));

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
                    if (d.getAnimalType() != null) {
                        dto.setAnimalTypeId(d.getAnimalType().getId());
                        dto.setAnimalTypeName(d.getAnimalType().getTypeName());
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
        }

        // Animal type info
        if (report.getAnimalType() != null) {
            dto.setAnimalTypeId(report.getAnimalType().getId());
            dto.setAnimalTypeName(report.getAnimalType().getTypeName());
        }
        dto.setAffectedCount(report.getAffectedCount());

        // Disease info
        if (report.getDisease() != null) {
            dto.setDiseaseId(report.getDisease().getId());
            dto.setDiseaseName(report.getDisease().getDiseaseName());
            dto.setDiseaseCode(report.getDisease().getDiseaseCode());
            dto.setSeverity(report.getDisease().getSeverity());
            dto.setIsNotifiable(report.getDisease().getIsNotifiable());
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
