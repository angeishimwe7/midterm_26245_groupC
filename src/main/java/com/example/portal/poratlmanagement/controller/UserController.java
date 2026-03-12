package com.example.portal.poratlmanagement.controller;

import com.example.portal.poratlmanagement.dto.PagedResponseDTO;
import com.example.portal.poratlmanagement.dto.UserDTO;
import com.example.portal.poratlmanagement.dto.UserRequestDTO;
import com.example.portal.poratlmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserController - REST API for User operations.
 * 
 * LOGIC EXPLANATION:
 * - Provides endpoints for user CRUD operations
 * - Includes endpoints to retrieve users by province (code or name)
 * - Manages Many-to-Many relationship with roles
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRequestDTO dto) {
        UserDTO created = userService.createUser(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Get all users with PAGINATION and SORTING
     */
    @GetMapping
    public ResponseEntity<PagedResponseDTO<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PagedResponseDTO<UserDTO> response = userService.getAllUsers(page, size, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * RETRIEVE USERS BY PROVINCE CODE
     * 
     * LOGIC EXPLANATION:
     * - Endpoint accepts province code as path variable
     * - Service layer calls repository with custom JPQL query
     * - Query joins User with Province and filters by province code
     * - Returns list of users belonging to that province
     */
    @GetMapping("/province/code/{provinceCode}")
    public ResponseEntity<List<UserDTO>> getUsersByProvinceCode(@PathVariable String provinceCode) {
        List<UserDTO> users = userService.getUsersByProvinceCode(provinceCode);
        return ResponseEntity.ok(users);
    }

    /**
     * RETRIEVE USERS BY PROVINCE CODE with PAGINATION
     */
    @GetMapping("/province/code/{provinceCode}/paged")
    public ResponseEntity<PagedResponseDTO<UserDTO>> getUsersByProvinceCodePaginated(
            @PathVariable String provinceCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PagedResponseDTO<UserDTO> response = userService.getUsersByProvinceCodePaginated(provinceCode, page, size, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }

    /**
     * RETRIEVE USERS BY PROVINCE NAME
     * 
     * LOGIC EXPLANATION:
     * - Similar to province code endpoint but uses province name
     * - Allows searching by human-readable province name
     * - Useful when code is not known
     */
    @GetMapping("/province/name/{provinceName}")
    public ResponseEntity<List<UserDTO>> getUsersByProvinceName(@PathVariable String provinceName) {
        List<UserDTO> users = userService.getUsersByProvinceName(provinceName);
        return ResponseEntity.ok(users);
    }

    /**
     * RETRIEVE USERS BY PROVINCE NAME with PAGINATION
     */
    @GetMapping("/province/name/{provinceName}/paged")
    public ResponseEntity<PagedResponseDTO<UserDTO>> getUsersByProvinceNamePaginated(
            @PathVariable String provinceName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PagedResponseDTO<UserDTO> response = userService.getUsersByProvinceNamePaginated(provinceName, page, size, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO dto) {
        UserDTO updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Assign role to user
     * Demonstrates Many-to-Many relationship management
     */
    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<UserDTO> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        UserDTO updated = userService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Remove role from user
     */
    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<UserDTO> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        UserDTO updated = userService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.ok(updated);
    }

    /**
     * EXISTSBY endpoints
     */
    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Map<String, Boolean>> existsByUsername(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Map<String, Boolean>> existsByEmail(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}
