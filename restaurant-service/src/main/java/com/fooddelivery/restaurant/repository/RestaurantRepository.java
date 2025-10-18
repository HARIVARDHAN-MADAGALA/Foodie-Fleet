package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * RESTAURANT REPOSITORY
 * 
 * Data Access Layer for Restaurant entity.
 * Extends JpaRepository which provides built-in methods like:
 * - save(), findById(), findAll(), deleteById(), etc.
 * 
 * Spring Data JPA automatically implements this interface at runtime.
 * No need to write implementation code!
 * 
 * Custom Query Methods:
 * Spring Data JPA generates queries based on method names.
 * Method naming convention: findBy + FieldName + Condition
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * Find restaurants by cuisine type
     * SQL Generated: SELECT * FROM restaurants WHERE cuisine = ?
     */
    List<Restaurant> findByCuisine(String cuisine);

    /**
     * Find only active restaurants
     * SQL Generated: SELECT * FROM restaurants WHERE is_active = true
     */
    List<Restaurant> findByIsActiveTrue();

    /**
     * Find restaurants by cuisine and active status
     * SQL Generated: SELECT * FROM restaurants WHERE cuisine = ? AND is_active = true
     */
    List<Restaurant> findByCuisineAndIsActiveTrue(String cuisine);

    /**
     * Search restaurants by name (partial match, case-insensitive)
     * SQL Generated: SELECT * FROM restaurants WHERE name LIKE %?%
     */
    List<Restaurant> findByNameContainingIgnoreCase(String name);

    /**
     * Find restaurants with rating greater than specified value
     * SQL Generated: SELECT * FROM restaurants WHERE rating >= ?
     */
    List<Restaurant> findByRatingGreaterThanEqual(Double rating);
}
