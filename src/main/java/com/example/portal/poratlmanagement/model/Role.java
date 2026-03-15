package com.example.portal.poratlmanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Role Entity - Represents user roles in the portal management system.
 * 
 * LOGIC EXPLANATION:
 * - This entity defines different roles that can be assigned to users
 * - Examples: ADMIN, USER, MANAGER, MODERATOR
 * - It demonstrates a Many-to-Many relationship with User entity
 * 
 * RELATIONSHIP:
 * - @ManyToMany: Many Roles can be assigned to Many Users
 * - mappedBy = "roles": User entity owns the relationship (has @JoinTable)
 * - This is the inverse side of the relationship
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", unique = true, nullable = false, length = 50)
    private String roleName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * Many-to-Many Relationship with User
     * - Many Roles can be assigned to Many Users
     * - mappedBy indicates User entity owns the relationship
     * - The join table 'user_roles' is defined in the User entity
     * - FetchType.LAZY for performance - users loaded only when needed
     * - @EqualsAndHashCode.Exclude prevents circular reference
     */
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    public Role(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
        this.isActive = true;
    }
}
