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
 * Entity representing a farm type in the system.
 * Farm types are lookup/reference data managed by administrators
 * to categorize different types of farming operations (Dairy, Beef, Poultry, etc.).
 * 
 * This entity follows the configuration pattern where:
 * - Administrators can create, update, and deactivate farm types
 * - Farm types are referenced by farms via foreign key relationship
 * - Deactivating a farm type affects all related farms
 */
@Entity
@Table(name = "farm_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message = "Farm type name is required")
    @Size(min = 2, max = 50, message = "Farm type name must be between 2 and 50 characters")
    @Column(name = "type_name", unique = true, nullable = false, length = 50)
    private String typeName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

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
     * Constructor for creating a new farm type with basic information.
     *
     * @param typeName the name of the farm type
     * @param description the description of the farm type
     */
    public FarmType(String typeName, String description) {
        this.typeName = typeName;
        this.description = description;
        this.isActive = true;
    }
}
