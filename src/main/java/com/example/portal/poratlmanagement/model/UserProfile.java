package com.example.portal.poratlmanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * UserProfile Entity - Represents additional profile information for a User.
 * 
 * LOGIC EXPLANATION:
 * - This entity stores extended user information separate from authentication details
 * - It demonstrates a One-to-One relationship with User entity
 * - Separating profile data from user data follows Single Responsibility Principle
 * 
 * RELATIONSHIP:
 * - @OneToOne: One UserProfile belongs to exactly One User
 * - @JoinColumn: Creates the foreign key 'user_id' in user_profiles table
 * - unique = true ensures one-to-one cardinality
 */
@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * One-to-One Relationship with User
     * - Each User has exactly one UserProfile
     * - @JoinColumn with unique=true enforces one-to-one relationship
     * - CascadeType.ALL: Changes to profile are cascaded to User if needed
     * - orphanRemoval = true: Profile is deleted when User is deleted
     * - @EqualsAndHashCode.Exclude prevents circular reference
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UserProfile(String firstName, String lastName, String phoneNumber, User user) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.user = user;
    }
}
