package com.fooddelivery.user.controller;

import com.fooddelivery.user.dto.AddressDTO;
import com.fooddelivery.user.dto.LoginRequest;
import com.fooddelivery.user.dto.UserDTO;
import com.fooddelivery.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * USER REST CONTROLLER
 * 
 * Handles all user-related HTTP requests
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * REGISTER NEW USER
     * POST /register
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO registeredUser = userService.registerUser(userDTO);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    /**
     * LOGIN USER
     * POST /login
     */
    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        UserDTO user = userService.loginUser(loginRequest);
        return ResponseEntity.ok(user);
    }

    /**
     * GET USER BY ID
     * GET /users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * GET ALL USERS
     * GET /users
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * UPDATE USER
     * PUT /users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * ADD ADDRESS
     * POST /users/{userId}/addresses
     */
    @PostMapping("/{userId}/addresses")
    public ResponseEntity<AddressDTO> addAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressDTO addressDTO) {
        addressDTO.setUserId(userId);
        AddressDTO savedAddress = userService.addAddress(addressDTO);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }

    /**
     * GET USER ADDRESSES
     * GET /users/{userId}/addresses
     */
    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses(@PathVariable Long userId) {
        List<AddressDTO> addresses = userService.getUserAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
