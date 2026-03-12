package com.example.portal.poratlmanagement.service;

import com.example.portal.poratlmanagement.dto.PagedResponseDTO;
import com.example.portal.poratlmanagement.dto.UserDTO;
import com.example.portal.poratlmanagement.dto.UserRequestDTO;
import com.example.portal.poratlmanagement.entity.Location;
import com.example.portal.poratlmanagement.entity.Role;
import com.example.portal.poratlmanagement.entity.User;
import com.example.portal.poratlmanagement.repository.LocationRepository;
import com.example.portal.poratlmanagement.repository.RoleRepository;
import com.example.portal.poratlmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UserService - Business logic for User operations.
 * 
 * LOGIC EXPLANATION:
 * - Handles user CRUD operations
 * - Implements retrieval by province (code or name)
 * - Manages Many-to-Many relationship with Roles
 * - Uses existBy for validation
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Create a new user
     * Uses existBy methods for validation
     * Handles Many-to-Many relationship with Roles
     */
    public UserDTO createUser(UserRequestDTO dto) {
        // EXISTSBY: Check if username already exists
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists: " + dto.getUsername());
        }

        // EXISTSBY: Check if email already exists
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists: " + dto.getEmail());
        }

        Location location = null;
        if (dto.getLocationId() != null) {
            location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + dto.getLocationId()));
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // In production, hash the password
        user.setLocation(location);
        user.setIsActive(true);

        // Handle Many-to-Many relationship with Roles
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(dto.getRoleIds()));
            user.setRoles(roles);
        }

        User saved = userRepository.save(user);
        return convertToDTO(saved);
    }

    /**
     * Get all users with pagination and sorting
     */
    public PagedResponseDTO<UserDTO> getAllUsers(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> userPage = userRepository.findAll(pageable);
        
        return convertToPagedResponse(userPage);
    }

    /**
     * Get user by ID
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToDTO(user);
    }

    /**
     * RETRIEVE USERS BY PROVINCE CODE:
     * 
     * LOGIC EXPLANATION:
     * - Calls repository method with custom JPQL query
     * - Query joins User with Province and filters by province code
     * - Returns all users belonging to the specified province
     * 
     * REPOSITORY METHOD USED:
     * - @Query("SELECT u FROM User u JOIN FETCH u.province p WHERE p.provinceCode = :provinceCode")
     * - Uses JOIN FETCH to load province data efficiently
     * - @Param binds method parameter to query parameter
     */
    public List<UserDTO> getUsersByProvinceCode(String provinceCode) {
        List<User> users = userRepository.findByProvinceCode(provinceCode);
        return users.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * RETRIEVE USERS BY PROVINCE CODE with PAGINATION
     */
    public PagedResponseDTO<UserDTO> getUsersByProvinceCodePaginated(String provinceCode, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> userPage = userRepository.findByProvinceCode(provinceCode, pageable);
        
        return convertToPagedResponse(userPage);
    }

    /**
     * RETRIEVE USERS BY PROVINCE NAME:
     * 
     * LOGIC EXPLANATION:
     * - Similar to getUsersByProvinceCode but uses province name
     * - Allows searching by human-readable province name
     * - Uses custom JPQL query in repository
     */
    public List<UserDTO> getUsersByProvinceName(String provinceName) {
        List<User> users = userRepository.findByProvinceName(provinceName);
        return users.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * RETRIEVE USERS BY PROVINCE NAME with PAGINATION
     */
    public PagedResponseDTO<UserDTO> getUsersByProvinceNamePaginated(String provinceName, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> userPage = userRepository.findByProvinceName(provinceName, pageable);
        
        return convertToPagedResponse(userPage);
    }

    /**
     * Update user
     */
    public UserDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Check username uniqueness if changed
        if (!user.getUsername().equals(dto.getUsername()) && userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists: " + dto.getUsername());
        }

        // Check email uniqueness if changed
        if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists: " + dto.getEmail());
        }

        Location location = null;
        if (dto.getLocationId() != null) {
            location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + dto.getLocationId()));
        }

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword());
        }
        user.setLocation(location);

        // Update roles
        if (dto.getRoleIds() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(dto.getRoleIds()));
            user.setRoles(roles);
        }

        User updated = userRepository.save(user);
        return convertToDTO(updated);
    }

    /**
     * Delete user
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Assign role to user
     * Demonstrates Many-to-Many relationship management
     */
    public UserDTO assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        user.addRole(role);
        User updated = userRepository.save(user);
        return convertToDTO(updated);
    }

    /**
     * Remove role from user
     */
    public UserDTO removeRoleFromUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        user.removeRole(role);
        User updated = userRepository.save(user);
        return convertToDTO(updated);
    }

    /**
     * EXISTSBY: Check if username exists
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * EXISTSBY: Check if email exists
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private PagedResponseDTO<UserDTO> convertToPagedResponse(Page<User> page) {
        List<UserDTO> content = page.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return new PagedResponseDTO<>(
            content,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        
        if (user.getLocation() != null) {
            dto.setLocationId(user.getLocation().getId());
            dto.setLocationName(user.getLocation().getName());
            dto.setLocationCode(user.getLocation().getCode());
            dto.setLocationType(user.getLocation().getType().name());
            // Build full hierarchy path
            dto.setLocationPath(buildLocationPath(user.getLocation()));
        }
        
        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }
    
    /**
     * Build full hierarchy path (e.g., "Kigali > Gasabo > Remera > Rukiri I > Umubano")
     */
    private String buildLocationPath(Location location) {
        StringBuilder path = new StringBuilder(location.getName());
        Location current = location.getParent();
        while (current != null) {
            path.insert(0, current.getName() + " > ");
            current = current.getParent();
        }
        return path.toString();
    }
}
