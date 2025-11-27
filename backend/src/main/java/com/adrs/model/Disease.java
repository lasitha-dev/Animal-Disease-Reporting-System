package com.adrs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a disease in the system.
 * Diseases are master data managed by administrators to track
 * various animal diseases that can be reported and monitored.
 * 
 * This entity follows the configuration pattern where:
 * - Administrators can create, update, and deactivate diseases
 * - Diseases are referenced by disease reports
 * - Each disease has severity level and notifiability status
 * - Affected animal types are stored as an array for flexibility
 */
@Entity
@Table(name = "diseases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Disease {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message = "Disease name is required")
    @Size(min = 2, max = 100, message = "Disease name must be between 2 and 100 characters")
    @Column(name = "disease_name", unique = true, nullable = false, length = 100)
    private String diseaseName;

    @Size(max = 20, message = "Disease code must not exceed 20 characters")
    @Column(name = "disease_code", unique = true, length = 20)
    private String diseaseCode;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Array of animal type IDs that can be affected by this disease.
     * Note: In PostgreSQL, this is stored as TEXT[]. 
     * In H2 (for testing), Hibernate serializes it as binary.
     * For production, use PostgreSQL where it's properly stored as an array.
     */
    @Column(name = "affected_animal_types")
    private String[] affectedAnimalTypes;

    /**
     * Severity level of the disease.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Severity severity;

    @Column(name = "is_notifiable", nullable = false)
    private Boolean isNotifiable = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * The animal type that can contract this disease.
     * Each disease is associated with a specific animal type.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_type_id")
    private AnimalType animalType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    /**
     * Constructor for creating a new disease with basic information.
     *
     * @param diseaseName the name of the disease
     * @param diseaseCode the unique code for the disease
     * @param description the description of the disease
     * @param severity the severity level
     * @param isNotifiable whether the disease is notifiable to authorities
     */
    public Disease(String diseaseName, String diseaseCode, String description, 
                   Severity severity, Boolean isNotifiable) {
        this.diseaseName = diseaseName;
        this.diseaseCode = diseaseCode;
        this.description = description;
        this.severity = severity;
        this.isNotifiable = isNotifiable;
        this.isActive = true;
    }

    /**
     * Enum representing disease severity levels.
     */
    public enum Severity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}
