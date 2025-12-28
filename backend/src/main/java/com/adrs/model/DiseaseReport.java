package com.adrs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a disease report in the system.
 * Disease reports track animal disease incidents and their outcomes.
 */
@Entity
@Table(name = "disease_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The animal type affected by this disease report.
     * Reports are made per animal type rather than individual animals.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_type_id", nullable = false)
    private AnimalType animalType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id", nullable = false)
    private Disease disease;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by", nullable = false)
    private User reportedBy;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    /**
     * Number of animals affected by this disease.
     */
    @Column(name = "affected_count")
    private Integer affectedCount;

    @Size(max = 2000, message = "Symptoms must not exceed 2000 characters")
    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Size(max = 2000, message = "Diagnosis must not exceed 2000 characters")
    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Size(max = 2000, message = "Treatment must not exceed 2000 characters")
    @Column(columnDefinition = "TEXT")
    private String treatment;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Outcome outcome;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Path to the uploaded image file (relative to storage root).
     */
    @Size(max = 255, message = "Image path must not exceed 255 characters")
    @Column(name = "image_path", length = 255)
    private String imagePath;

    @Column(name = "is_confirmed", nullable = false)
    private Boolean isConfirmed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by")
    private User confirmedBy;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Enum representing disease report outcomes.
     */
    public enum Outcome {
        RECOVERED,
        DIED,
        ONGOING,
        EUTHANIZED
    }
}

