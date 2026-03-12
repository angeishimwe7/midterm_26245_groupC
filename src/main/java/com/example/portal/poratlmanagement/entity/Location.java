package com.example.portal.poratlmanagement.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Location Entity - Represents all administrative levels in Rwanda's hierarchy.
 * 
 * LOGIC EXPLANATION:
 * - Single table stores all levels: PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE
 * - Uses 'type' column to distinguish different levels
 * - Self-referencing relationship creates the hierarchy (parent_id)
 * - Province has no parent (parent = null)
 * - District's parent is Province
 * - Sector's parent is District
 * - Cell's parent is Sector
 * - Village's parent is Cell
 * 
 * RELATIONSHIP:
 * - @ManyToOne: Each location has one parent (except Province)
 * - @OneToMany: Each location can have multiple children
 * - This creates a tree structure for the administrative hierarchy
 */
@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    /**
     * LocationType enum - Defines the administrative level
     */
    public enum LocationType {
        PROVINCE,    // Level 1: Top level
        DISTRICT,    // Level 2: Belongs to Province
        SECTOR,      // Level 3: Belongs to District
        CELL,        // Level 4: Belongs to Sector
        VILLAGE      // Level 5: Belongs to Cell (lowest level)
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", unique = true, nullable = false, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Type of location - Determines the administrative level
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private LocationType type;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * Self-referencing Many-to-One: Parent location
     * - Province has no parent (null)
     * - District's parent is Province
     * - Sector's parent is District
     * - Cell's parent is Sector
     * - Village's parent is Cell
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Location parent;

    /**
     * Self-referencing One-to-Many: Child locations
     * - Province has many Districts
     * - District has many Sectors
     * - Sector has many Cells
     * - Cell has many Villages
     * - Village has no children (empty list)
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Location> children = new ArrayList<>();

    public Location(String code, String name, LocationType type, Location parent) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.parent = parent;
        this.isActive = true;
    }

    /**
     * Helper method to add a child location
     */
    public void addChild(Location child) {
        children.add(child);
        child.setParent(this);
    }

    /**
     * Helper method to get the Province (top ancestor)
     */
    public Location getProvince() {
        Location current = this;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return current.getType() == LocationType.PROVINCE ? current : null;
    }
}
