package com.example.portal.poratlmanagement.service;

import com.example.portal.poratlmanagement.dto.UserProfileDTO;
import com.example.portal.poratlmanagement.model.User;
import com.example.portal.poratlmanagement.model.UserProfile;
import com.example.portal.poratlmanagement.repository.UserProfileRepository;
import com.example.portal.poratlmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserProfileService - Business logic for UserProfile operations.
 * 
 * LOGIC EXPLANATION:
 * - Handles One-to-One relationship between User and UserProfile
 * - Demonstrates how to create and manage profile data
 * - Uses existBy for validation
 */
@Service
@Transactional
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create user profile
     * 
     * ONE-TO-ONE RELATIONSHIP HANDLING:
     * - Each User can have exactly one UserProfile
     * - Profile is created separately and linked to User
     * - @JoinColumn in UserProfile creates the foreign key
     */
    public UserProfileDTO createProfile(Long userId, UserProfileDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // EXISTSBY: Check if user already has a profile
        if (userProfileRepository.existsByUserId(userId)) {
            throw new RuntimeException("User already has a profile");
        }

        // EXISTSBY: Check if phone number is already used
        if (dto.getPhoneNumber() != null && userProfileRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists: " + dto.getPhoneNumber());
        }

        UserProfile profile = new UserProfile();
        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setDateOfBirth(dto.getDateOfBirth());
        profile.setBio(dto.getBio());
        profile.setAvatarUrl(dto.getAvatarUrl());
        profile.setUser(user);  // Set One-to-One relationship

        UserProfile saved = userProfileRepository.save(profile);
        return convertToDTO(saved);
    }

    /**
     * Get profile by user ID
     */
    public UserProfileDTO getProfileByUserId(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found for user id: " + userId));
        return convertToDTO(profile);
    }

    /**
     * Get profile by username
     */
    public UserProfileDTO getProfileByUsername(String username) {
        UserProfile profile = userProfileRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Profile not found for username: " + username));
        return convertToDTO(profile);
    }

    /**
     * Update profile
     */
    public UserProfileDTO updateProfile(Long profileId, UserProfileDTO dto) {
        UserProfile profile = userProfileRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("Profile not found with id: " + profileId));

        // Check phone number uniqueness if changed
        if (dto.getPhoneNumber() != null && 
            !dto.getPhoneNumber().equals(profile.getPhoneNumber()) &&
            userProfileRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists: " + dto.getPhoneNumber());
        }

        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setDateOfBirth(dto.getDateOfBirth());
        profile.setBio(dto.getBio());
        profile.setAvatarUrl(dto.getAvatarUrl());

        UserProfile updated = userProfileRepository.save(profile);
        return convertToDTO(updated);
    }

    /**
     * Delete profile
     */
    public void deleteProfile(Long profileId) {
        if (!userProfileRepository.existsById(profileId)) {
            throw new RuntimeException("Profile not found with id: " + profileId);
        }
        userProfileRepository.deleteById(profileId);
    }

    /**
     * Check if user has profile
     * EXISTSBY: Returns boolean
     */
    public boolean hasProfile(Long userId) {
        return userProfileRepository.existsByUserId(userId);
    }

    private UserProfileDTO convertToDTO(UserProfile profile) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(profile.getId());
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setDateOfBirth(profile.getDateOfBirth());
        dto.setBio(profile.getBio());
        dto.setAvatarUrl(profile.getAvatarUrl());
        
        if (profile.getUser() != null) {
            dto.setUserId(profile.getUser().getId());
            dto.setUsername(profile.getUser().getUsername());
        }
        
        return dto;
    }
}
