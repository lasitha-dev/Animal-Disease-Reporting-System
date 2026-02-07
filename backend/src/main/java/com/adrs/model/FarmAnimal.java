package com.adrs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an animal type count for a farm.
 * This links a farm to animal types with the count of animals of that type.
 */
@Entity
@Table(name = "farm_animals", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"farm_id", "animal_type_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"farm", "animalType"})
@EqualsAndHashCode(of = "id")
public class FarmAnimal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    @NotNull(message = "Farm is required")
    private Farm farm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_type_id", nullable = false)
    @NotNull(message = "Animal type is required")
    private AnimalType animalType;

    @Min(value = 0, message = "Count must be a positive number")
    @Column(nullable = false)
    private Integer count = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Constructor for creating a farm animal entry.
     *
     * @param farm the farm
     * @param animalType the animal type
     * @param count the count of animals
     */
    public FarmAnimal(Farm farm, AnimalType animalType, Integer count) {
        this.farm = farm;
        this.animalType = animalType;
        this.count = count;
    }
}
