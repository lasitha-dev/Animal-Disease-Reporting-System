package com.adrs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a farm in the system.
 * Farms are the primary locations where animals are kept and managed.
 */
@Entity
@Table(name = "farms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"farmType", "farmAnimals", "createdBy", "updatedBy"})
@EqualsAndHashCode(of = "id")
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message = "Farm name is required")
    @Size(max = 100, message = "Farm name must not exceed 100 characters")
    @Column(name = "farm_name", nullable = false, length = 100)
    private String farmName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_type_id", nullable = false)
    @NotNull(message = "Farm type is required")
    private FarmType farmType;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Size(max = 100, message = "Owner name must not exceed 100 characters")
    @Column(name = "owner_name", length = 100)
    private String ownerName;

    @Pattern(regexp = "^(\\+94[0-9]{9})?$", message = "Contact must be a valid Sri Lankan number (+94XXXXXXXXX)")
    @Column(name = "owner_contact", length = 20)
    private String ownerContact;

    @NotBlank(message = "Address is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String address;

    @NotNull(message = "Province is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Province province;

    @NotNull(message = "District is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private District district;

    @Column(name = "gps_latitude", precision = 10, scale = 8)
    private BigDecimal gpsLatitude;

    @Column(name = "gps_longitude", precision = 11, scale = 8)
    private BigDecimal gpsLongitude;

    @Column(name = "total_animals")
    private Integer totalAnimals = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FarmAnimal> farmAnimals = new ArrayList<>();

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
}

