package com.adrs.service;

import com.adrs.dto.AnimalTypeDTO;
import com.adrs.dto.DiseaseDTO;
import com.adrs.dto.DiseaseMapDTO;
import com.adrs.dto.DiseaseReportRequestDTO;
import com.adrs.dto.DiseaseReportResponseDTO;
import com.adrs.model.Province;
import com.adrs.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Disease Report management.
 */
public interface DiseaseReportService {

    /**
     * Create a new disease report.
     *
     * @param request the report request data
     * @param image optional image file
     * @param reporter the vet creating the report
     * @return the created report
     */
    DiseaseReportResponseDTO createReport(DiseaseReportRequestDTO request, MultipartFile image, User reporter);

    /**
     * Get a disease report by ID.
     *
     * @param id the report ID
     * @return the report
     */
    DiseaseReportResponseDTO getReportById(UUID id);

    /**
     * Get all disease reports by the current vet.
     *
     * @param vet the vet user
     * @return list of reports
     */
    List<DiseaseReportResponseDTO> getReportsByVet(User vet);

    /**
     * Get all disease reports for a specific farm.
     *
     * @param farmId the farm ID
     * @return list of reports
     */
    List<DiseaseReportResponseDTO> getReportsByFarm(UUID farmId);

    /**
     * Get count of reports by a vet.
     *
     * @param vet the vet user
     * @return count of reports
     */
    Long getReportCountByVet(User vet);

    /**
     * Update a disease report.
     *
     * @param id the report ID
     * @param request the updated request data
     * @param image optional new image file
     * @param updater the user updating the report
     * @return the updated report
     */
    DiseaseReportResponseDTO updateReport(UUID id, DiseaseReportRequestDTO request, MultipartFile image, User updater);

    /**
     * Delete a disease report.
     *
     * @param id the report ID
     * @param user the user deleting the report
     */
    void deleteReport(UUID id, User user);

    /**
     * Get disease reports grouped by farm for map display.
     *
     * @param animalTypeIds optional list of animal type IDs to filter by
     * @param diseaseIds optional list of disease IDs to filter by
     * @param province optional province to filter by
     * @return list of disease data grouped by farm location
     */
    List<DiseaseMapDTO> getReportsForMap(List<UUID> animalTypeIds, List<UUID> diseaseIds, Province province);

    /**
     * Get animal types that have disease reports with GPS coordinates.
     *
     * @return list of animal types with disease reports
     */
    List<AnimalTypeDTO> getAnimalTypesWithReports();

    /**
     * Get diseases that have reports with GPS coordinates, filtered by animal type IDs.
     *
     * @param animalTypeIds optional list of animal type IDs to filter by
     * @return list of diseases with disease reports
     */
    List<DiseaseDTO> getDiseasesWithReports(List<UUID> animalTypeIds);

    /**
     * Get all disease reports NOT created by the current vet.
     *
     * @param vet the current vet user
     * @return list of reports by other vets
     */
    List<DiseaseReportResponseDTO> getReportsNotByVet(User vet);
}
