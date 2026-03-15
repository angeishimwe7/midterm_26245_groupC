package com.example.portal.poratlmanagement.config;

import com.example.portal.poratlmanagement.model.*;
import com.example.portal.poratlmanagement.model.Location.LocationType;
import com.example.portal.poratlmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * DataInitializer - Initializes sample data on application startup.
 * 
 * LOGIC EXPLANATION:
 * - Implements CommandLineRunner to run on startup
 * - Creates Rwanda administrative structure using SINGLE Location table
 * - Hierarchy: Province → District → Sector → Cell → Village
 * - Uses LocationType enum and parent_id to create relationships
 * - Creates Users linked to Village locations
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("Initializing Rwanda Administrative Structure with Single Location Table...");

        // ============================================
        // Create Rwanda Administrative Hierarchy
        // Using SINGLE Location table with type column
        // ============================================

        // Create Provinces (Level 1) - No parent
        Location kigali = createLocation("KG", "Kigali", LocationType.PROVINCE, null, "Capital Province");
        Location north = createLocation("NO", "North", LocationType.PROVINCE, null, "Northern Province");
        Location south = createLocation("SO", "South", LocationType.PROVINCE, null, "Southern Province");

        // Create Districts (Level 2) - Parent is Province
        Location gasabo = createLocation("GA", "Gasabo", LocationType.DISTRICT, kigali, "Kigali District");
        Location kicukiro = createLocation("KC", "Kicukiro", LocationType.DISTRICT, kigali, "Kigali District");
        Location nyarugenge = createLocation("NY", "Nyarugenge", LocationType.DISTRICT, kigali, "Kigali District");

        // Create Sectors (Level 3) - Parent is District
        Location remera = createLocation("RE", "Remera", LocationType.SECTOR, gasabo, "Gasabo Sector");
        Location gisozi = createLocation("GI", "Gisozi", LocationType.SECTOR, gasabo, "Gasabo Sector");
        Location kacyiru = createLocation("KA", "Kacyiru", LocationType.SECTOR, gasabo, "Gasabo Sector");

        // Create Cells (Level 4) - Parent is Sector
        Location rukiri1 = createLocation("R1", "Rukiri I", LocationType.CELL, remera, "Remera Cell");
        Location rukiri2 = createLocation("R2", "Rukiri II", LocationType.CELL, remera, "Remera Cell");
        Location nyarutarama = createLocation("NYA", "Nyarutarama", LocationType.CELL, remera, "Remera Cell");

        // Create Villages (Level 5) - Parent is Cell
        Location umubano = createLocation("UM", "Umubano", LocationType.VILLAGE, rukiri1, "Rukiri I Village");
        Location ubumwe = createLocation("UB", "Ubumwe", LocationType.VILLAGE, rukiri1, "Rukiri I Village");
        Location unity = createLocation("UN", "Unity", LocationType.VILLAGE, rukiri2, "Rukiri II Village");
        Location peace = createLocation("PE", "Peace", LocationType.VILLAGE, nyarutarama, "Nyarutarama Village");

        System.out.println("Rwanda Administrative Structure created with Single Location Table!");
        System.out.println("Hierarchy: Province → District → Sector → Cell → Village");

        // ============================================
        // Create Roles
        // ============================================
        Role adminRole = createRole("ADMIN", "System Administrator with full access");
        Role userRole = createRole("USER", "Regular user with limited access");
        Role managerRole = createRole("MANAGER", "Manager with elevated privileges");

        // ============================================
        // Create Users - Linked to Village Locations
        // ============================================
        // IMPORTANT: Users are linked to Villages (Location with type=VILLAGE)
        // Through parent_id hierarchy: Village → Cell → Sector → District → Province
        
        User admin = createUser("admin", "admin@portal.com", "password123", umubano);
        User john = createUser("john_doe", "john@example.com", "password123", ubumwe);
        User jane = createUser("jane_smith", "jane@example.com", "password123", unity);
        User manager = createUser("manager1", "manager@portal.com", "password123", peace);

        // Assign Roles (Many-to-Many)
        admin.addRole(adminRole);
        admin.addRole(userRole);
        john.addRole(userRole);
        jane.addRole(userRole);
        jane.addRole(managerRole);
        manager.addRole(managerRole);

        userRepository.save(admin);
        userRepository.save(john);
        userRepository.save(jane);
        userRepository.save(manager);

        // Create UserProfiles (One-to-One with User)
        createUserProfile("Admin", "User", "0781234567", LocalDate.of(1990, 1, 15), admin);
        createUserProfile("John", "Doe", "0782345678", LocalDate.of(1985, 5, 20), john);
        createUserProfile("Jane", "Smith", "0783456789", LocalDate.of(1988, 8, 10), jane);
        createUserProfile("Manager", "One", "0784567890", LocalDate.of(1982, 3, 25), manager);

        System.out.println("\n=== Portal Management System Ready ===");
        System.out.println("API Base URL: http://localhost:8082/api");
        System.out.println("\nRwanda Administrative Structure (Single Location Table):");
        System.out.println("- 3 Provinces (Kigali, North, South)");
        System.out.println("- 3 Districts in Kigali (Gasabo, Kicukiro, Nyarugenge)");
        System.out.println("- 3 Sectors in Gasabo (Remera, Gisozi, Kacyiru)");
        System.out.println("- 3 Cells in Remera (Rukiri I, Rukiri II, Nyarutarama)");
        System.out.println("- 4 Villages created");
        System.out.println("\nUsers are linked to Village Locations!");
        System.out.println("Through the hierarchy: Village → Cell → Sector → District → Province");
        System.out.println("\nTest endpoints:");
        System.out.println("- GET /api/locations/type/PROVINCE (Get all Provinces)");
        System.out.println("- GET /api/locations/type/DISTRICT (Get all Districts)");
        System.out.println("- GET /api/locations/{id}/children (Get children of a location)");
        System.out.println("- GET /api/users/province/code/KG (Get users by Province code)");
    }

    /**
     * Create Location with hierarchy
     * Uses single Location table with type and parent_id
     */
    private Location createLocation(String code, String name, LocationType type, Location parent, String description) {
        Location location = new Location();
        location.setCode(code);
        location.setName(name);
        location.setType(type);
        location.setParent(parent);
        location.setDescription(description);
        location.setIsActive(true);
        return locationRepository.save(location);
    }

    private Role createRole(String name, String description) {
        Role role = new Role(name, description);
        return roleRepository.save(role);
    }

    /**
     * Create User linked to a Location (typically a Village)
     */
    private User createUser(String username, String email, String password, Location location) {
        User user = new User(username, email, password, location);
        return userRepository.save(user);
    }

    private UserProfile createUserProfile(String firstName, String lastName, String phone, LocalDate dob, User user) {
        UserProfile profile = new UserProfile(firstName, lastName, phone, user);
        profile.setDateOfBirth(dob);
        profile.setBio("Profile for " + firstName + " " + lastName);
        return userProfileRepository.save(profile);
    }
}
