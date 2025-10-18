package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * MENU ITEM REPOSITORY
 * 
 * Data Access Layer for MenuItem entity.
 * Provides database operations for menu items.
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    /**
     * Find all menu items for a specific restaurant
     * SQL: SELECT * FROM menu_items WHERE restaurant_id = ?
     */
    List<MenuItem> findByRestaurantId(Long restaurantId);

    /**
     * Find available menu items for a restaurant
     * SQL: SELECT * FROM menu_items WHERE restaurant_id = ? AND is_available = true
     */
    List<MenuItem> findByRestaurantIdAndIsAvailableTrue(Long restaurantId);

    /**
     * Find menu items by category
     * SQL: SELECT * FROM menu_items WHERE category = ?
     */
    List<MenuItem> findByCategory(String category);

    /**
     * Find vegetarian menu items for a restaurant
     * SQL: SELECT * FROM menu_items WHERE restaurant_id = ? AND is_vegetarian = true
     */
    List<MenuItem> findByRestaurantIdAndIsVegetarianTrue(Long restaurantId);
}
