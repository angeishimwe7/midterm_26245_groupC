package com.example.portal.poratlmanagement.controller;

import com.example.portal.poratlmanagement.dto.UserProfileDTO;
import com.example.portal.poratlmanagement.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * UserProfileController - REST API for UserProfile operations.
 * 
 * LOGIC EXPLANATION:
 * - Provides endpoints for user profile CRUD operations
 * - Demonstrates One-to-One relationship management
 * - Uses existBy for validation
 */
@RestController
@RequestMapping("/api/profiles")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    /**
     * Create profile for a user
     * Demonstrates One-to-One relationship creation
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<UserProfileDTO> createProfile(
            @PathVariable Long userId,
            @RequestBody UserProfileDTO dto) {
        UserProfileDTO created = userProfileService.createProfile(userId, dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserProfileDTO> getProfileByUserId(@PathVariable Long userId) {
        UserProfileDTO profile = userProfileService.getProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserProfileDTO> getProfileByUsername(@PathVariable String username) {
        UserProfileDTO profile = userProfileService.getProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{profileId}")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @PathVariable Long profileId,
            @RequestBody UserProfileDTO dto) {
        UserProfileDTO updated = userProfileService.updateProfile(profileId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long profileId) {
        userProfileService.deleteProfile(profileId);
        return ResponseEntity.noContent().build();
    }

    /**
     * EXISTSBY endpoint: Check if user has profile
     */
    @GetMapping("/user/{userId}/exists")
    public ResponseEntity<Map<String, Boolean>> hasProfile(@PathVariable Long userId) {
        boolean hasProfile = userProfileService.hasProfile(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasProfile", hasProfile);
        return ResponseEntity.ok(response);
    }
}
