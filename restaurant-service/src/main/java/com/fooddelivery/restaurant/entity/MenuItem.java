package com.fooddelivery.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * MENU ITEM ENTITY
 * 
 * Represents a food item in a restaurant's menu.
 * Maps to 'menu_items' table in database.
 * 
 * Relationship: Many menu items belong to one restaurant
 */
@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double price;

    private String category;  // e.g., Appetizer, Main Course, Dessert, Beverage

    @Column(nullable = false)
    private Boolean isAvailable = true;

    private String imageUrl;

    private Boolean isVegetarian = false;

    /**
     * MANY-TO-ONE RELATIONSHIP
     * Many menu items belong to one restaurant
     * 
     * @ManyToOne: Defines many-to-one relationship
     * @JoinColumn: Specifies the foreign key column name
     * @JsonIgnore: Prevents infinite recursion during JSON serialization
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIgnore
    private Restaurant restaurant;
}
