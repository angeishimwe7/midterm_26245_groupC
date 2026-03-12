package com.example.portal.poratlmanagement.service;

import com.example.portal.poratlmanagement.dto.RoleDTO;
import com.example.portal.poratlmanagement.entity.Role;
import com.example.portal.poratlmanagement.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RoleService - Business logic for Role operations.
 * 
 * LOGIC EXPLANATION:
 * - Manages roles for Many-to-Many relationship with Users
 * - Uses existBy for validation
 */
@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Create a new role
     */
    public RoleDTO createRole(RoleDTO dto) {
        // EXISTSBY: Check if role name already exists
        if (roleRepository.existsByRoleName(dto.getRoleName())) {
            throw new RuntimeException("Role already exists: " + dto.getRoleName());
        }

        Role role = new Role();
        role.setRoleName(dto.getRoleName());
        role.setDescription(dto.getDescription());
        role.setIsActive(true);

        Role saved = roleRepository.save(role);
        return convertToDTO(saved);
    }

    /**
     * Get all roles
     */
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get role by ID
     */
    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        return convertToDTO(role);
    }

    /**
     * Get role by name
     */
    public RoleDTO getRoleByName(String roleName) {
        Role role = roleRepository.findByRoleName(roleName)
            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        return convertToDTO(role);
    }

    /**
     * Update role
     */
    public RoleDTO updateRole(Long id, RoleDTO dto) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        // Check uniqueness if name changed
        if (!role.getRoleName().equals(dto.getRoleName()) && roleRepository.existsByRoleName(dto.getRoleName())) {
            throw new RuntimeException("Role already exists: " + dto.getRoleName());
        }

        role.setRoleName(dto.getRoleName());
        role.setDescription(dto.getDescription());

        Role updated = roleRepository.save(role);
        return convertToDTO(updated);
    }

    /**
     * Delete role
     * Checks if role is assigned to any user before deletion
     */
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        // Check if role has users assigned
        if (!role.getUsers().isEmpty()) {
            throw new RuntimeException("Cannot delete role that is assigned to users");
        }

        roleRepository.deleteById(id);
    }

    /**
     * Check if role exists
     * EXISTSBY: Returns boolean
     */
    public boolean existsByRoleName(String roleName) {
        return roleRepository.existsByRoleName(roleName);
    }

    private RoleDTO convertToDTO(Role role) {
        return new RoleDTO(
            role.getId(),
            role.getRoleName(),
            role.getDescription(),
            role.getIsActive()
        );
    }
}
