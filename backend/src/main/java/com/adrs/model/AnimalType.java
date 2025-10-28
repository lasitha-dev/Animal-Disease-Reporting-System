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
 * Entity representing an animal type in the system.
 * Animal types are lookup/reference data managed by administrators
 * to categorize different types of animals (Cattle, Buffalo, Goat, Sheep, etc.).
 * 
 * This entity follows the configuration pattern where:
 * - Administrators can create, update, and deactivate animal types
 * - Animal types are referenced by animals via foreign key relationship
 * - Deactivating an animal type affects all related animals
 */
@Entity
@Table(name = "animal_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message = "Animal type name is required")
    @Size(min = 2, max = 50, message = "Animal type name must be between 2 and 50 characters")
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

    /**
     * Constructor for creating a new animal type with basic information.
     *
     * @param typeName the name of the animal type
     * @param description the description of the animal type
     */
    public AnimalType(String typeName, String description) {
        this.typeName = typeName;
        this.description = description;
        this.isActive = true;
    }
}
