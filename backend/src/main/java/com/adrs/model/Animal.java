package com.adrs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an animal in the system.
 * Animals belong to farms and have specific types.
 */
@Entity
@Table(name = "animals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_type_id", nullable = false)
    private AnimalType animalType;

    @Size(max = 50, message = "Tag number must not exceed 50 characters")
    @Column(name = "tag_number", unique = true, length = 50)
    private String tagNumber;

    @Size(max = 50, message = "Breed must not exceed 50 characters")
    @Column(length = 50)
    private String breed;

    @Column(name = "age_years")
    private Integer ageYears;

    @Column(name = "age_months")
    private Integer ageMonths;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Sex sex;

    @Size(max = 20, message = "Health status must not exceed 20 characters")
    @Column(name = "health_status", length = 20)
    private String healthStatus = "HEALTHY";

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    /**
     * Enum representing animal sex.
     */
    public enum Sex {
        MALE,
        FEMALE,
        UNKNOWN
    }
}
