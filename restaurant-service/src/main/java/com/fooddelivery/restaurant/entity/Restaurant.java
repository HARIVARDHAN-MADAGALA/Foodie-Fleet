package com.fooddelivery.restaurant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * RESTAURANT ENTITY
 * 
 * Represents a restaurant in the system.
 * This class maps to the 'restaurants' table in MySQL database.
 * 
 * Annotations Explained:
 * @Entity: Marks this class as a JPA entity (database table)
 * @Table: Specifies the table name in database
 * @Data: Lombok annotation - generates getters, setters, toString, equals, hashCode
 * @NoArgsConstructor: Lombok - generates no-argument constructor
 * @AllArgsConstructor: Lombok - generates constructor with all fields
 * 
 * Implements Serializable: Required for Redis caching
 */
@Entity
@Table(name = "restaurants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class  Restaurant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String cuisine;  // e.g., Italian, Chinese, Indian, etc.

    @Column(nullable = false)
    private String address;

    private String phone;

    private String email;

    @Column(nullable = false)
    private Boolean isActive = true;

    private Double rating = 0.0;  // Average rating

    private Integer totalRatings = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    /**
     * ONE-TO-MANY RELATIONSHIP
     * One restaurant can have many menu items
     * 
     * @OneToMany: Defines one-to-many relationship
     * mappedBy: Specifies the field in MenuItem that owns the relationship
     * cascade: Operations on Restaurant cascade to MenuItem (delete restaurant = delete menu items)
     * orphanRemoval: If a menu item is removed from list, it's deleted from database
     */
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuItem> menuItems = new ArrayList<>();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
