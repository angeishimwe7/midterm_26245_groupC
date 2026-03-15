package com.example.portal.poratlmanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * User Entity - Represents a user in the portal management system.
 * 
 * LOGIC EXPLANATION:
 * - This is the central entity that connects to multiple other entities
 * - Demonstrates One-to-One, Many-to-One, and Many-to-Many relationships
 * - Contains authentication and basic user information
 * 
 * RELATIONSHIPS:
 * 1. @ManyToOne with Province - Many Users belong to One Province
 * 2. @OneToOne with UserProfile - One User has One Profile (bidirectional)
 * 3. @ManyToMany with Role - Many Users have Many Roles
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Many-to-One Relationship with Location (Village type)
     * - Many Users can belong to One Village (Location with type=VILLAGE)
     * - @JoinColumn creates foreign key 'location_id' in users table
     * - This is the KEY relationship for Rwanda's administrative structure
     * - When creating a User, you only need to specify the Village location
     * - Through Location hierarchy (parent_id), User is automatically linked to:
     *   Village → Cell → Sector → District → Province
     * - This allows retrieving users by Province, District, Sector, Cell, or Village
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Location location;

    /**
     * One-to-One Relationship with UserProfile
     * - One User has exactly one UserProfile
     * - mappedBy = "user" indicates UserProfile owns the relationship
     * - CascadeType.ALL: Operations cascade to the profile
     * - orphanRemoval = true: Profile is deleted when User is deleted
     * - @EqualsAndHashCode.Exclude prevents circular reference
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private UserProfile userProfile;

    /**
     * Many-to-Many Relationship with Role
     * - Many Users can have Many Roles
     * - @JoinTable defines the join table 'user_roles'
     * - joinColumns: foreign key to User (user_id)
     * - inverseJoinColumns: foreign key to Role (role_id)
     * - This creates a separate table to manage the many-to-many relationship
     * - @EqualsAndHashCode.Exclude prevents circular reference
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public User(String username, String email, String password, Location location) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.location = location;
        this.isActive = true;
    }

    /**
     * Helper method to add a role to the user
     * Maintains the bidirectional relationship
     */
    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsers().add(this);
    }

    /**
     * Helper method to remove a role from the user
     * Maintains the bidirectional relationship
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getUsers().remove(this);
    }

    /**
     * Helper method to set user profile
     * Establishes the bidirectional relationship
     */
    public void setUserProfile(UserProfile profile) {
        this.userProfile = profile;
        if (profile != null) {
            profile.setUser(this);
        }
    }
}
