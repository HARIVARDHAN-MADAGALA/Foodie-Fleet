package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.dto.MenuItemDTO;
import com.fooddelivery.restaurant.dto.RestaurantDTO;
import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * RESTAURANT REST CONTROLLER
 * 
 * Handles HTTP requests for restaurant operations.
 * 
 * Annotations:
 * @RestController: Combines @Controller and @ResponseBody
 * @RequestMapping: Base path for all endpoints in this controller
 * @RequiredArgsConstructor: Lombok - constructor injection for dependencies
 * 
 * HTTP Methods:
 * GET: Retrieve data
 * POST: Create new data
 * PUT: Update existing data
 * DELETE: Remove data
 * 
 * Response Status Codes:
 * 200 OK: Success
 * 201 CREATED: Resource created successfully
 * 404 NOT FOUND: Resource not found
 * 500 INTERNAL SERVER ERROR: Server error
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class RestaurantController {


    private final RestaurantService restaurantService;


    /**
     * CREATE NEW RESTAURANT
     * <p>
     * Endpoint: POST /restaurants
     * Request Body: RestaurantDTO (JSON)
     * Response: Created restaurant with ID
     * <p>
     * Example Request:
     * POST http://localhost:8081/restaurants
     * {
     * "name": "Pizza Paradise",
     * "cuisine": "Italian",
     * "address": "123 Main Street",
     * "phone": "1234567890",
     * "email": "info@pizzaparadise.com"
     * }
     */
    @PostMapping("/restaurants")
    public ResponseEntity<RestaurantDTO> createRestaurant(@Valid @RequestBody RestaurantDTO restaurantDTO) {
        RestaurantDTO createdRestaurant = restaurantService.createRestaurant(restaurantDTO);
        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }

    /**
     * GET RESTAURANT BY ID
     * <p>
     * Endpoint: GET /restaurants/{id}
     * Path Variable: id (restaurant ID)
     * Response: Restaurant details
     * <p>
     * Example: GET http://localhost:8081/restaurants/1
     */
    @GetMapping("/restaurants/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable Long id) {
        RestaurantDTO restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    /**
     * GET ALL RESTAURANTS
     * <p>
     * Endpoint: GET /restaurants
     * Response: List of all restaurants
     * <p>
     * Example: GET http://localhost:8081/restaurants
     */
    @GetMapping("/restaurants")
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        List<RestaurantDTO> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    /**
     * SEARCH RESTAURANTS BY CUISINE
     * <p>
     * Endpoint: GET /restaurants/cuisine/{cuisine}
     * Path Variable: cuisine (e.g., Italian, Chinese)
     * Response: List of restaurants with matching cuisine
     * <p>
     * Example: GET http://localhost:8081/restaurants/cuisine/Italian
     */
    @GetMapping("/restaurants/cuisine/{cuisine}")
    public ResponseEntity<List<RestaurantDTO>> getRestaurantsByCuisine(@PathVariable String cuisine) {
        List<RestaurantDTO> restaurants = restaurantService.getRestaurantsByCuisine(cuisine);
        return ResponseEntity.ok(restaurants);
    }

    /**
     * SEARCH RESTAURANTS BY NAME
     * <p>
     * Endpoint: GET /restaurants/search?name=pizza
     * Query Parameter: name (partial name search)
     * Response: List of matching restaurants
     * <p>
     * Example: GET http://localhost:8081/restaurants/search?name=pizza
     */
    @GetMapping("/restaurants/search")
    public ResponseEntity<List<RestaurantDTO>> searchRestaurants(@RequestParam String name) {
        List<RestaurantDTO> restaurants = restaurantService.searchRestaurantsByName(name);
        return ResponseEntity.ok(restaurants);
    }

    /**
     * UPDATE RESTAURANT
     * <p>
     * Endpoint: PUT /restaurants/{id}
     * Path Variable: id (restaurant ID)
     * Request Body: Updated restaurant data
     * Response: Updated restaurant
     * <p>
     * Example: PUT http://localhost:8081/restaurants/1
     */
    @PutMapping("/restaurants/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantDTO restaurantDTO) {
        RestaurantDTO updatedRestaurant = restaurantService.updateRestaurant(id, restaurantDTO);
        return ResponseEntity.ok(updatedRestaurant);
    }

    /**
     * DELETE RESTAURANT
     * <p>
     * Endpoint: DELETE /restaurants/{id}
     * Path Variable: id (restaurant ID)
     * Response: 204 No Content
     * <p>
     * Example: DELETE http://localhost:8081/restaurants/1
     */
    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ADD MENU ITEM TO RESTAURANT
     * <p>
     * Endpoint: POST /restaurants/{restaurantId}/menu
     * Path Variable: restaurantId
     * Request Body: MenuItemDTO
     * Response: Created menu item
     * <p>
     * Example: POST http://localhost:8081/restaurants/1/menu
     * {
     * "name": "Margherita Pizza",
     * "description": "Classic pizza with tomato and mozzarella",
     * "price": 299.99,
     * "category": "Main Course",
     * "isVegetarian": true,
     * "restaurantId": 1
     * }
     */
    @PostMapping("/restaurants/{restaurantId}/menu")
    public ResponseEntity<MenuItemDTO> addMenuItem(
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuItemDTO menuItemDTO) {
        menuItemDTO.setRestaurantId(restaurantId);
        MenuItemDTO createdMenuItem = restaurantService.addMenuItem(menuItemDTO);
        return new ResponseEntity<>(createdMenuItem, HttpStatus.CREATED);
    }

    /**
     * GET MENU ITEMS FOR RESTAURANT
     * <p>
     * Endpoint: GET /restaurants/{restaurantId}/menu
     * Path Variable: restaurantId
     * Response: List of menu items
     * <p>
     * Example: GET http://localhost:8081/restaurants/1/menu
     */
    @GetMapping("/restaurants/{restaurantId}/menu")
    public ResponseEntity<List<MenuItemDTO>> getMenuItems(@PathVariable Long restaurantId) {
        List<MenuItemDTO> menuItems = restaurantService.getMenuItems(restaurantId);
        return ResponseEntity.ok(menuItems);
    }

    /**
     * UPDATE RESTAURANT RATING
     * Internal endpoint called by Rating Service
     * <p>
     * Endpoint: PUT /restaurants/{restaurantId}/rating
     * Request Parameter: rating value
     */
    @PutMapping("/restaurants/{restaurantId}/rating")
    public ResponseEntity<Void> updateRating(
            @PathVariable Long restaurantId,
            @RequestParam Double rating) {
        restaurantService.updateRating(restaurantId, rating);
        return ResponseEntity.ok().build();
    }

    /**
     * GET ALL MENU ITEMS
     *
     * Endpoint: GET /restaurants/menu
     * Response: List of menu items
     *
     * Example: GET http://localhost:8081/restaurants/menu
     */

    @GetMapping("/restaurants/menu")
    public ResponseEntity<List<MenuItemDTO>> getAllMenus(){
        List<MenuItemDTO> menuItems = restaurantService.getAllMenuitems();
        return ResponseEntity.ok(menuItems);
    }

    /**
     * GLOBAL EXCEPTION HANDLER
     * Catches all RuntimeExceptions and returns appropriate HTTP response
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

}
