package com.fooddelivery.user.service;

import com.fooddelivery.user.dto.AddressDTO;
import com.fooddelivery.user.dto.LoginRequest;
import com.fooddelivery.user.dto.UserDTO;
import com.fooddelivery.user.entity.Address;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.repository.AddressRepository;
import com.fooddelivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * USER SERVICE LAYER
 * 
 * Handles business logic for:
 * - User registration and authentication
 * - User profile management
 * - Address management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    /**
     * REGISTER NEW USER
     */
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        log.info("Registering new user: {}", userDTO.getEmail());

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        if (userRepository.existsByPhone(userDTO.getPhone())) {
            throw new RuntimeException("Phone number already registered");
        }

        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setPassword(userDTO.getPassword());  // In production: use BCryptPasswordEncoder
        user.setIsActive(true);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return convertToDTO(savedUser);
    }

    /**
     * LOGIN USER
     * Simple authentication - checks email and password
     * In production: Use Spring Security with JWT tokens
     */
    @Cacheable(value = "users", key = "#loginRequest.email")
    public UserDTO loginUser(LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());

        User user = userRepository.findByEmailAndPassword(
                        loginRequest.getEmail(),
                        loginRequest.getPassword())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!user.getIsActive()) {
            throw new RuntimeException("User account is inactive");
        }

        log.info("User logged in successfully: {}", user.getEmail());
        return convertToDTO(user);
    }

    /**
     * GET USER BY ID
     */
    @Cacheable(value = "users", key = "#id")
    public UserDTO getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        return convertToDTO(user);
    }

    /**
     * UPDATE USER PROFILE
     */
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(userDTO.getName());
        user.setPhone(userDTO.getPhone());

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully");

        return convertToDTO(updatedUser);
    }

    /**
     * ADD DELIVERY ADDRESS
     */
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public AddressDTO addAddress(AddressDTO addressDTO) {
        log.info("Adding address for user ID: {}", addressDTO.getUserId());

        User user = userRepository.findById(addressDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.TRUE.equals(addressDTO.getIsDefault())) {
            List<Address> existingAddresses = addressRepository.findByUserId(user.getId());
            existingAddresses.forEach(addr -> addr.setIsDefault(false));
            addressRepository.saveAll(existingAddresses);
        }

        Address address = new Address();
        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setPincode(addressDTO.getPincode());
        address.setLandmark(addressDTO.getLandmark());
        address.setAddressType(addressDTO.getAddressType());
        address.setIsDefault(addressDTO.getIsDefault());
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        log.info("Address added successfully with ID: {}", savedAddress.getId());

        return convertToAddressDTO(savedAddress);
    }

    /**
     * GET USER ADDRESSES
     */
    public List<AddressDTO> getUserAddresses(Long userId) {
        log.info("Fetching addresses for user ID: {}", userId);

        return addressRepository.findByUserId(userId).stream()
                .map(this::convertToAddressDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET ALL USERS
     */
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users");

        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setIsActive(user.getIsActive());
        return dto;
    }

    private AddressDTO convertToAddressDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setAddressLine1(address.getAddressLine1());
        dto.setAddressLine2(address.getAddressLine2());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setPincode(address.getPincode());
        dto.setLandmark(address.getLandmark());
        dto.setAddressType(address.getAddressType());
        dto.setIsDefault(address.getIsDefault());
        dto.setUserId(address.getUser().getId());
        return dto;
    }
}
