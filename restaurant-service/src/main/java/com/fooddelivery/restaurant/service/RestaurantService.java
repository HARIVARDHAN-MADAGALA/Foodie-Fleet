package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.dto.MenuItemDTO;
import com.fooddelivery.restaurant.dto.RestaurantDTO;
import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.MenuItemRepository;
import com.fooddelivery.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RESTAURANT SERVICE LAYER
 * 
 * Business Logic Layer - Contains all business logic for restaurant operations.
 * 
 * Annotations:
 * @Service: Marks this as a Spring service component
 * @RequiredArgsConstructor: Lombok - generates constructor for final fields (dependency injection)
 * @Slf4j: Lombok - provides logger instance (log.info(), log.error(), etc.)
 * @Transactional: Ensures database operations are atomic (all or nothing)
 * 
 * Caching Annotations:
 * @Cacheable: Stores method result in Redis cache
 * @CacheEvict: Removes data from cache when it's modified
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    /**
     * CREATE RESTAURANT
     * Converts DTO to Entity and saves to database
     */
    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public RestaurantDTO createRestaurant(RestaurantDTO restaurantDTO) {
        log.info("Creating new restaurant: {}", restaurantDTO.getName());
        
        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantDTO.getName());
        restaurant.setCuisine(restaurantDTO.getCuisine());
        restaurant.setAddress(restaurantDTO.getAddress());
        restaurant.setPhone(restaurantDTO.getPhone());
        restaurant.setEmail(restaurantDTO.getEmail());
        restaurant.setIsActive(true);
        
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant created successfully with ID: {}", savedRestaurant.getId());
        
        return convertToDTO(savedRestaurant);
    }

    /**
     * GET RESTAURANT BY ID
     * Uses Redis cache - if restaurant is in cache, returns from cache
     * Otherwise, fetches from database and stores in cache
     */
    @Cacheable(value = "restaurants", key = "#id")
    public RestaurantDTO getRestaurantById(Long id) {
        log.info("Fetching restaurant with ID: {}", id);
        
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + id));
        
        return convertToDTO(restaurant);
    }

    /**
     * GET ALL RESTAURANTS
     * Cached with key "all"
     */
    @Cacheable(value = "restaurants", key = "'all'")
    public List<RestaurantDTO> getAllRestaurants() {
        log.info("Fetching all restaurants");
        
        return restaurantRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * SEARCH RESTAURANTS BY CUISINE
     */
    public List<RestaurantDTO> getRestaurantsByCuisine(String cuisine) {
        log.info("Searching restaurants by cuisine: {}", cuisine);
        
        return restaurantRepository.findByCuisineAndIsActiveTrue(cuisine).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * SEARCH RESTAURANTS BY NAME
     */
    public List<RestaurantDTO> searchRestaurantsByName(String name) {
        log.info("Searching restaurants by name: {}", name);
        
        return restaurantRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * UPDATE RESTAURANT
     * Evicts (removes) old data from cache
     */
    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public RestaurantDTO updateRestaurant(Long id, RestaurantDTO restaurantDTO) {
        log.info("Updating restaurant with ID: {}", id);
        
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + id));
        
        restaurant.setName(restaurantDTO.getName());
        restaurant.setCuisine(restaurantDTO.getCuisine());
        restaurant.setAddress(restaurantDTO.getAddress());
        restaurant.setPhone(restaurantDTO.getPhone());
        restaurant.setEmail(restaurantDTO.getEmail());
        
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant updated successfully");
        
        return convertToDTO(updatedRestaurant);
    }

    /**
     * DELETE RESTAURANT
     * Evicts cache entry
     */
    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public void deleteRestaurant(Long id) {
        log.info("Deleting restaurant with ID: {}", id);
        
        if (!restaurantRepository.existsById(id)) {
            throw new RuntimeException("Restaurant not found with ID: " + id);
        }
        
        restaurantRepository.deleteById(id);
        log.info("Restaurant deleted successfully");
    }

    /**
     * ADD MENU ITEM TO RESTAURANT
     */
    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public MenuItemDTO addMenuItem(MenuItemDTO menuItemDTO) {
        log.info("Adding menu item to restaurant ID: {}", menuItemDTO.getRestaurantId());
        
        Restaurant restaurant = restaurantRepository.findById(menuItemDTO.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        
        MenuItem menuItem = new MenuItem();
        menuItem.setName(menuItemDTO.getName());
        menuItem.setDescription(menuItemDTO.getDescription());
        menuItem.setPrice(menuItemDTO.getPrice());
        menuItem.setCategory(menuItemDTO.getCategory());
        menuItem.setIsAvailable(true);
        menuItem.setImageUrl(menuItemDTO.getImageUrl());
        menuItem.setIsVegetarian(menuItemDTO.getIsVegetarian());
        menuItem.setRestaurant(restaurant);
        
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        log.info("Menu item added successfully with ID: {}", savedMenuItem.getId());
        
        return convertToMenuItemDTO(savedMenuItem);
    }

    /**
     * GET MENU ITEMS FOR A RESTAURANT
     */
    public List<MenuItemDTO> getMenuItems(Long restaurantId) {
        log.info("Fetching menu items for restaurant ID: {}", restaurantId);
        
        return menuItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId).stream()
                .map(this::convertToMenuItemDTO)
                .collect(Collectors.toList());
    }

    /**
     * UPDATE RESTAURANT RATING
     * Called by Rating Service
     */
    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public void updateRating(Long restaurantId, Double newRating) {
        log.info("Updating rating for restaurant ID: {}", restaurantId);
        
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        
        int currentTotal = restaurant.getTotalRatings();
        double currentRating = restaurant.getRating();
        
        // Calculate new average rating
        double totalRatingPoints = (currentRating * currentTotal) + newRating;
        int newTotalRatings = currentTotal + 1;
        double averageRating = totalRatingPoints / newTotalRatings;
        
        restaurant.setRating(Math.round(averageRating * 10.0) / 10.0); // Round to 1 decimal
        restaurant.setTotalRatings(newTotalRatings);
        
        restaurantRepository.save(restaurant);
        log.info("Rating updated successfully. New average: {}", restaurant.getRating());
    }

    /**
     * HELPER METHOD: Convert Entity to DTO
     */
    private RestaurantDTO convertToDTO(Restaurant restaurant) {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setCuisine(restaurant.getCuisine());
        dto.setAddress(restaurant.getAddress());
        dto.setPhone(restaurant.getPhone());
        dto.setEmail(restaurant.getEmail());
        dto.setIsActive(restaurant.getIsActive());
        dto.setRating(restaurant.getRating());
        dto.setTotalRatings(restaurant.getTotalRatings());
        return dto;
    }

    /**
     * HELPER METHOD: Convert MenuItem Entity to DTO
     */
    private MenuItemDTO convertToMenuItemDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        dto.setCategory(menuItem.getCategory());
        dto.setIsAvailable(menuItem.getIsAvailable());
        dto.setImageUrl(menuItem.getImageUrl());
        dto.setIsVegetarian(menuItem.getIsVegetarian());
        dto.setRestaurantId(menuItem.getRestaurant().getId());
        return dto;
    }
}
