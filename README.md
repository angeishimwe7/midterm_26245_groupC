# Portal Management System

## Rwanda Administrative Hierarchy Implementation

A comprehensive Spring Boot application demonstrating advanced JPA relationships, pagination, sorting, and RESTful API design using Rwanda's administrative structure as a real-world use case.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Business Context](#business-context)
3. [Architecture & Design](#architecture--design)
4. [Technology Stack](#technology-stack)
5. [Database Design](#database-design)
6. [Entity Relationships Explained](#entity-relationships-explained)
7. [Implementation Details](#implementation-details)
8. [API Documentation](#api-documentation)
9. [Getting Started Guide](#getting-started-guide)
10. [Testing the APIs](#testing-the-apis)
11. [Assessment Requirements Mapping](#assessment-requirements-mapping)
12. [Troubleshooting](#troubleshooting)

---

## Project Overview

This project implements a **Portal Management System** that manages users and their locations within Rwanda's administrative hierarchy. The system demonstrates enterprise-grade Spring Boot development patterns including:

- Single Table Inheritance for hierarchical data
- Self-referencing relationships
- Pagination and sorting
- Complex JPQL queries
- DTO pattern for API responses
- Data initialization on startup

### Key Features

| Feature | Description |
|---------|-------------|
| **Hierarchical Location Management** | 5-level hierarchy: Province → District → Sector → Cell → Village |
| **User Management** | CRUD operations with role-based access |
| **Advanced Queries** | Retrieve users by province code/name through hierarchy traversal |
| **Pagination & Sorting** | Efficient data retrieval for large datasets |
| **Relationship Mapping** | All JPA relationship types demonstrated |

---

## Business Context

### Rwanda Administrative Structure

Rwanda is organized into a 5-level administrative hierarchy:

```
┌─────────────────────────────────────────────────────────────┐
│                    RWANDA ADMINISTRATIVE                    │
│                       HIERARCHY                             │
├─────────────────────────────────────────────────────────────┤
│  Level 1: PROVINCE (Intara)                                  │
│     └── Level 2: DISTRICT (Akarere)                         │
│            └── Level 3: SECTOR (Umurenge)                   │
│                   └── Level 4: CELL (Akagari)               │
│                          └── Level 5: VILLAGE (Umudugudu)   │
└─────────────────────────────────────────────────────────────┘
```

### Design Decision: Single Table vs Multiple Tables

**Why we chose Single Table approach:**

| Approach | Pros | Cons |
|----------|------|------|
| **Single Table** (Our Choice) | Simple queries, easy hierarchy traversal, better performance for reads | Nullable columns, less normalization |
| **Multiple Tables** | Full normalization, strict typing | Complex joins, harder hierarchy queries |

**Our Implementation:**
- One `locations` table with `type` column (PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE)
- Self-referencing `parent_id` for hierarchy
- UUID for primary and foreign keys

---

## Architecture & Design

### System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      CLIENT (Postman/Browser)               │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP Requests
┌──────────────────────▼──────────────────────────────────────┐
│              REST CONTROLLERS                               │
│  LocationController    UserController    RoleController     │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│              SERVICE LAYER                                  │
│  LocationService       UserService       RoleService        │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│              REPOSITORY LAYER                               │
│  LocationRepository    UserRepository    RoleRepository     │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│              DATABASE (PostgreSQL)                          │
│  locations    users    user_profiles    roles    user_roles │
└─────────────────────────────────────────────────────────────┘
```

### Design Patterns Used

1. **DTO (Data Transfer Object) Pattern**: Separate entities from API responses
2. **Repository Pattern**: Data access abstraction
3. **Service Layer Pattern**: Business logic encapsulation
4. **Dependency Injection**: Spring's IoC container

---

## Technology Stack

### Core Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Programming language |
| Spring Boot | 4.0.3 | Application framework |
| Spring Data JPA | 4.0.3 | Data persistence |
| Hibernate | 7.2.4.Final | ORM implementation |
| PostgreSQL | 17+ | Relational database |
| Maven | 3.8+ | Build tool |
| Lombok | Latest | Boilerplate reduction |

### Key Dependencies (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    
    <!-- Utilities -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```

---

## Database Design

### Entity Relationship Diagram (ERD)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              ERD DIAGRAM                                │
└─────────────────────────────────────────────────────────────────────────┘

┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│   locations  │         │    users     │         │ user_profiles│
├──────────────┤         ├──────────────┤         ├──────────────┤
│ PK id (UUID) │◄────────┤ FK location_id│         │ PK id (Long) │
│ FK parent_id │         │ PK id (Long) │◄────────┤ FK user_id   │
│    code      │         │   username   │         │  first_name  │
│    name      │         │    email     │         │  last_name   │
│    type      │         │   password   │         │ phone_number │
│ description  │         │  is_active   │         │  created_at  │
│  is_active   │         │  created_at  │         │  updated_at  │
└──────────────┘         │  updated_at  │         └──────────────┘
         ▲               └──────────────┘
         │                        │
         │               ┌────────┴────────┐
         │               │   user_roles    │
         │               ├─────────────────┤
         │               │ FK user_id (PK) │
         │               │ FK role_id (PK) │
         │               └─────────────────┘
         │                        │
         │               ┌────────▼────────┐
         │               │     roles       │
         │               ├─────────────────┤
         │               │ PK id (Long)    │
         │               │   role_name     │
         │               │   description   │
         │               │   is_active     │
         │               └─────────────────┘
         │
         │    Self-Referencing Relationship
         │    (Location Hierarchy)
         │
    ┌────┴─────────────────────────────────────┐
    │  Province (parent_id = NULL)             │
    │     └── District (parent_id = Province)  │
    │            └── Sector (parent_id = Dist) │
    │                   └── Cell (parent_id =  │
    │                          └── Village     │
    └──────────────────────────────────────────┘
```

### Table Schemas

#### 1. Locations Table
```sql
CREATE TABLE locations (
    id UUID PRIMARY KEY,                    -- Auto-generated UUID
    parent_id UUID REFERENCES locations(id), -- Self-referencing FK
    code VARCHAR(20) UNIQUE NOT NULL,       -- Unique code (e.g., "KG", "GA")
    name VARCHAR(100) NOT NULL,             -- Display name
    type VARCHAR(20) NOT NULL CHECK (       -- Enum constraint
        type IN ('PROVINCE', 'DISTRICT', 'SECTOR', 'CELL', 'VILLAGE')
    ),
    description VARCHAR(255),               -- Optional description
    is_active BOOLEAN DEFAULT TRUE          -- Soft delete flag
);
```

**Indexes:**
- Primary Key: `id`
- Unique: `code`
- Foreign Key: `parent_id` → `locations(id)`
- Check Constraint: `type` enum values

#### 2. Users Table
```sql
CREATE TABLE users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    location_id UUID REFERENCES locations(id),  -- Links to Village
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

#### 3. User Profiles Table
```sql
CREATE TABLE user_profiles (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone_number VARCHAR(20),
    date_of_birth DATE,
    bio VARCHAR(500),
    avatar_url VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

#### 4. Roles Table
```sql
CREATE TABLE roles (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE
);
```

#### 5. User Roles Join Table
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);
```

---

## Entity Relationships Explained

### 1. Self-Referencing Relationship (Location Hierarchy)

**Purpose:** Create the 5-level administrative hierarchy

**Implementation:**
```java
@Entity
@Table(name = "locations")
public class Location {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // Many-to-One: Each location has one parent (except Province)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Location parent;
    
    // One-to-Many: Each location can have many children
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Location> children = new ArrayList<>();
    
    // Helper method to add child
    public void addChild(Location child) {
        children.add(child);
        child.setParent(this);
    }
}
```

**Logic:**
- Province: `parent = null` (top of hierarchy)
- District: `parent = Province`
- Sector: `parent = District`
- Cell: `parent = Sector`
- Village: `parent = Cell`

### 2. Many-to-Many Relationship (User-Role)

**Purpose:** Assign multiple roles to users

**Implementation:**
```java
// User.java (Owning Side)
@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
@JoinTable(
    name = "user_roles",                    // Join table name
    joinColumns = @JoinColumn(name = "user_id"),      // FK to User
    inverseJoinColumns = @JoinColumn(name = "role_id") // FK to Role
)
private Set<Role> roles = new HashSet<>();

// Role.java (Inverse Side)
@ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
private Set<User> users = new HashSet<>();
```

**Join Table:** `user_roles`
| user_id | role_id |
|---------|---------|
| 1       | 1       |
| 1       | 2       |
| 2       | 2       |

### 3. One-to-One Relationship (User-UserProfile)

**Purpose:** Separate authentication from profile data

**Implementation:**
```java
// User.java (Inverse Side)
@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
private UserProfile userProfile;

// UserProfile.java (Owning Side)
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", unique = true, nullable = false)
private User user;
```

**Foreign Key Location:** `user_profiles.user_id`

### 4. Many-to-One Relationship (User-Location)

**Purpose:** Link users to their village location

**Implementation:**
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "location_id")
private Location location;
```

**Foreign Key Location:** `users.location_id`

---

## Implementation Details

### 1. Saving Location with Hierarchy

**File:** `LocationService.java`

```java
public LocationDTO createLocation(LocationDTO dto) {
    // Step 1: Validate parent exists (except for PROVINCE)
    Location parent = null;
    if (dto.getParentId() != null) {
        parent = locationRepository.findById(dto.getParentId())
            .orElseThrow(() -> new RuntimeException("Parent not found"));
    }
    
    // Step 2: Check code uniqueness (existBy pattern)
    if (locationRepository.existsByCode(dto.getCode())) {
        throw new RuntimeException("Code already exists");
    }
    
    // Step 3: Create and save location
    Location location = new Location();
    location.setCode(dto.getCode());
    location.setName(dto.getName());
    location.setType(LocationType.valueOf(dto.getType()));
    location.setParent(parent);  // Set hierarchy
    
    return convertToDTO(locationRepository.save(location));
}
```

### 2. Pagination and Sorting

**File:** `LocationService.java`

```java
public PagedResponseDTO<LocationDTO> getAllLocations(int page, int size, String sortBy, String sortDir) {
    // Create Sort object
    Sort sort = sortDir.equalsIgnoreCase("desc") 
        ? Sort.by(sortBy).descending() 
        : Sort.by(sortBy).ascending();
    
    // Create Pageable with page number, size, and sort
    Pageable pageable = PageRequest.of(page, size, sort);
    
    // Execute paginated query
    Page<Location> locationPage = locationRepository.findAll(pageable);
    
    return convertToPagedResponse(locationPage);
}
```

**Performance Benefits:**
- Reduces memory usage (only loads requested page)
- Faster response times
- Better user experience with large datasets
- Database optimizes with LIMIT/OFFSET

### 3. existBy() Implementation

**File:** `UserRepository.java`

```java
// Spring Data JPA automatically generates EXISTS query
boolean existsByUsername(String username);
boolean existsByEmail(String email);

// Generated SQL:
// SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)
```

**Why use existsBy() instead of count()?**
- `existsBy()` stops at first match (faster)
- `count()` scans entire table (slower)

### 4. Retrieving Users by Province

**File:** `UserRepository.java`

```java
@Query("SELECT u FROM User u JOIN FETCH u.location l " +
       "WHERE l.parent.parent.parent.parent.code = :provinceCode " +
       "AND l.parent.parent.parent.parent.type = 'PROVINCE'")
List<User> findByProvinceCode(@Param("provinceCode") String provinceCode);
```

**Query Logic:**
```
User → Location (Village) → Cell → Sector → District → Province
  u       l                    ↑      ↑        ↑         ↑
  │       │                    │      │        │         │
  │       └── l.parent ────────┘      │        │         │
  │              └── l.parent.parent ─┘        │         │
  │                     └── l.parent.parent.parent ────┘  │
  │                            └── l.parent.parent.parent.parent
  └──────────────────────────────────────────────────────────────► Province
```

**Hierarchy Traversal:**
- `l` = Village (user's direct location)
- `l.parent` = Cell
- `l.parent.parent` = Sector
- `l.parent.parent.parent` = District
- `l.parent.parent.parent.parent` = Province

---

## API Documentation

### Base URL
```
http://localhost:8089/api
```

### Location APIs

#### Create Location
```http
POST /api/locations
Content-Type: application/json

{
    "code": "GA",
    "name": "Gasabo",
    "type": "DISTRICT",
    "description": "Kigali District",
    "parentId": "41271b76-df90-4202-bc82-fe9aa81b4..."
}
```

**Response:**
```json
{
    "id": "c5fb3f32-1ac0-4a92-932b-0b88558dc...",
    "code": "GA",
    "name": "Gasabo",
    "type": "DISTRICT",
    "description": "Kigali District",
    "parentId": "41271b76-df90-4202-bc82-fe9aa81b4...",
    "parentName": "Kigali",
    "fullPath": "Kigali > Gasabo"
}
```

#### Get All Locations (Paginated)
```http
GET /api/locations?page=0&size=10&sortBy=name&sortDir=asc
```

**Response:**
```json
{
    "content": [...],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 16,
    "totalPages": 2,
    "last": false
}
```

#### Get Locations by Type
```http
GET /api/locations/type/PROVINCE
```

#### Get Children of a Location
```http
GET /api/locations/{parentId}/children
```

### User APIs

#### Create User
```http
POST /api/users
Content-Type: application/json

{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "locationId": "d8f9a2b1-...",
    "roleIds": [2]
}
```

#### Get Users by Province Code
```http
GET /api/users/province/code/KG
```

#### Check Username Exists
```http
GET /api/users/exists/username/john_doe
```

**Response:**
```json
{
    "exists": true
}
```

---

## Getting Started Guide

### Prerequisites

1. **Java 21** - [Download](https://adoptium.net/)
2. **PostgreSQL 17+** - [Download](https://www.postgresql.org/download/)
3. **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
4. **Postman** (optional) - [Download](https://www.postman.com/downloads/)

### Step-by-Step Setup

#### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/poratlmanagement.git
cd poratlmanagement
```

#### 2. Create Database
```bash
# Open psql
psql -U postgres

# Create database
CREATE DATABASE portal_db;

# Exit
\q
```

#### 3. Configure Application
Edit `src/main/resources/application.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/portal_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Server Port
server.port=8089
```

#### 4. Build and Run
```bash
# Using Maven Wrapper (Windows)
.\mvnw.cmd clean compile spring-boot:run

# Using Maven Wrapper (Linux/Mac)
./mvnw clean compile spring-boot:run

# Or using installed Maven
mvn clean compile spring-boot:run
```

#### 5. Verify Installation
```bash
curl http://localhost:8089/api/locations/type/PROVINCE
```

---

## Testing the APIs

### Using Postman

1. **Import Collection** - Create a new collection with these requests:

#### Test 1: Get All Provinces
```
GET http://localhost:8089/api/locations/type/PROVINCE
```

#### Test 2: Create a District
```
POST http://localhost:8089/api/locations
Body (raw JSON):
{
    "code": "NY",
    "name": "Nyarugenge",
    "type": "DISTRICT",
    "parentId": "{province-uuid}"
}
```

#### Test 3: Get Users by Province
```
GET http://localhost:8089/api/users/province/code/KG
```

#### Test 4: Check Existence
```
GET http://localhost:8089/api/locations/exists?code=KG
```

### Using cURL

```bash
# Get provinces
curl -X GET http://localhost:8089/api/locations/type/PROVINCE

# Create location
curl -X POST http://localhost:8089/api/locations \
  -H "Content-Type: application/json" \
  -d '{"code":"TEST","name":"Test Location","type":"VILLAGE","parentId":"uuid-here"}'

# Get users by province
curl -X GET http://localhost:8089/api/users/province/code/KG
```

---

## Assessment Requirements Mapping

| Requirement | Marks | Implementation Location | Evidence |
|-------------|-------|------------------------|----------|
| **1. ERD with 5 tables** | 3 | Database Schema | locations, users, user_profiles, roles, user_roles |
| **2. Saving Location** | 2 | `LocationService.createLocation()` | Validates parent, checks code uniqueness, saves with hierarchy |
| **3. Sorting & Pagination** | 5 | `LocationService.getAllLocations()` | Uses `PageRequest.of()` and `Sort.by()` |
| **4. Many-to-Many** | 3 | `User.java` lines 90-98 | `user_roles` join table with `@JoinTable` |
| **5. One-to-Many** | 2 | `Location.java` lines 109-112 | Self-referencing with `mappedBy="parent"` |
| **6. One-to-One** | 2 | `User.java` lines 76-79 | `UserProfile` with `mappedBy` and `orphanRemoval` |
| **7. existBy()** | 2 | `UserRepository.java` lines 112-122 | `existsByUsername()`, `existsByEmail()` |
| **8. Users by Province** | 4 | `UserRepository.java` lines 49-68 | `findByProvinceCode()`, `findByProvinceName()` |
| **TOTAL** | **23/23** | ✅ Complete | |

---

## Troubleshooting

### Common Issues

#### 1. Port Already in Use
**Error:** `Port 8089 was already in use`

**Solution:**
```properties
# Change port in application.properties
server.port=8090
```

#### 2. Database Connection Failed
**Error:** `Connection refused`

**Solution:**
```bash
# Start PostgreSQL service
# Windows:
net start postgresql-x64-17

# Linux:
sudo service postgresql start

# Verify connection
psql -U postgres -d portal_db
```

#### 3. UUID Conversion Error
**Error:** `cannot convert Long to UUID`

**Cause:** Using old Long IDs instead of UUID

**Solution:** All Location IDs must be UUID format:
```json
{
    "parentId": "41271b76-df90-4202-bc82-fe9aa81b4123"
}
```

#### 4. Lazy Initialization Exception
**Error:** `could not initialize proxy`

**Solution:** Use `JOIN FETCH` in queries or `@Transactional`

---

## Sample Data Initialized

On application startup, the following data is created:

### Provinces
| Code | Name | UUID (Example) |
|------|------|----------------|
| KG | Kigali | 41271b76-df90-4202-bc82-fe9aa81b4... |
| NO | North | a1b2c3d4-e5f6-7890-abcd-ef123456... |
| SO | South | b2c3d4e5-f6a7-8901-bcde-f2345678... |

### Users
| Username | Email | Location | Roles |
|----------|-------|----------|-------|
| admin | admin@portal.com | Umubano Village | ADMIN, USER |
| john_doe | john@example.com | Ubumwe Village | USER |
| jane_smith | jane@example.com | Unity Village | USER, MANAGER |
| manager1 | manager@portal.com | Peace Village | MANAGER |

---

## License

This project is for educational purposes - Practical Assessment.

## Author

Portal Management System - Rwanda Administrative Hierarchy Implementation

---

## Additional Resources

- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Hibernate Documentation](https://hibernate.org/documentation/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Rwanda Administrative Structure](https://www.gov.rw/)
