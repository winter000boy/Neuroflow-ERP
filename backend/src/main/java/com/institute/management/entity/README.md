# JPA Entities Documentation

## Overview

This package contains all the core JPA entities for the Educational Institute Management System. The entities are designed to support the complete workflow from lead management to student placement tracking.

## Entity Relationships

```
Course (1) ←→ (N) Batch (N) ←→ (1) Employee (Instructor)
   ↑                ↓
   └─────────── Student (N) ←→ (1) Lead ←→ (1) Employee (Counsellor)
                    ↓
               Placement (N) ←→ (1) Company
                    
Employee (1) ←→ (1) User
```

## Core Entities

### 1. Course
- **Purpose**: Represents educational programs offered by the institute
- **Key Fields**: name, description, duration (months), fees, status
- **Relationships**: One-to-Many with Batch
- **Enums**: CourseStatus (ACTIVE, INACTIVE, ARCHIVED)

### 2. Company
- **Purpose**: Partner organizations for student placements
- **Key Fields**: name, industry, contact details, partnership date
- **Relationships**: One-to-Many with Placement
- **Enums**: CompanyStatus (ACTIVE, INACTIVE, BLACKLISTED)

### 3. Employee
- **Purpose**: Institute staff members with different roles
- **Key Fields**: employee code, personal details, role, department, hire date
- **Relationships**: 
  - One-to-One with User
  - One-to-Many with Lead (as counsellor)
  - One-to-Many with Batch (as instructor)
- **Enums**: 
  - EmployeeRole (ADMIN, COUNSELLOR, FACULTY, PLACEMENT_OFFICER, OPERATIONS)
  - EmployeeStatus (ACTIVE, INACTIVE, TERMINATED)

### 4. User
- **Purpose**: Authentication credentials for employees
- **Key Fields**: username, password (BCrypt), login tracking, account security
- **Relationships**: One-to-One with Employee
- **Features**: Account locking, password expiry, failed login tracking
- **Enums**: UserStatus (ACTIVE, INACTIVE, LOCKED, SUSPENDED)

### 5. Batch
- **Purpose**: Class groupings for course delivery
- **Key Fields**: name, capacity, enrollment tracking, dates
- **Relationships**: 
  - Many-to-One with Course
  - Many-to-One with Employee (instructor)
  - One-to-Many with Student
- **Features**: Capacity management, utilization calculation, automatic end date calculation
- **Enums**: BatchStatus (PLANNED, ACTIVE, COMPLETED, CANCELLED)

### 6. Lead
- **Purpose**: Potential students in the sales pipeline
- **Key Fields**: contact information, course interest, source, status
- **Relationships**: 
  - Many-to-One with Employee (counsellor)
  - One-to-Many with Student (converted leads)
- **Features**: Follow-up tracking, conversion tracking
- **Embedded**: FollowUp (date, notes, next action)
- **Enums**: LeadStatus (NEW, CONTACTED, INTERESTED, NOT_INTERESTED, CONVERTED, LOST)

### 7. Student
- **Purpose**: Enrolled students in the institute
- **Key Fields**: enrollment number, personal details, academic information
- **Relationships**: 
  - Many-to-One with Batch
  - Many-to-One with Lead (source)
  - One-to-Many with Placement
- **Features**: Status history tracking, graduation management
- **Embedded**: StatusHistory (status, change date, notes)
- **Enums**: StudentStatus (ACTIVE, INACTIVE, GRADUATED, DROPPED_OUT, SUSPENDED)

### 8. Placement
- **Purpose**: Job placement records for students
- **Key Fields**: position, salary, dates, employment details
- **Relationships**: 
  - Many-to-One with Student
  - Many-to-One with Company
- **Features**: Probation tracking, tenure calculation
- **Enums**: 
  - PlacementStatus (PLACED, RESIGNED, TERMINATED, COMPLETED)
  - JobType (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE)
  - EmploymentType (PERMANENT, TEMPORARY, PROBATION, CONSULTANT)

## Database Configuration

### Development Environment
- **Profile**: `dev`
- **DDL Mode**: `create-drop` (auto-generates schema)
- **Database**: PostgreSQL (localhost:5432/institute_management_dev)

### Production Environment
- **Profile**: `prod`
- **DDL Mode**: `validate` (requires existing schema)
- **Migration**: Use `V1__Create_initial_schema.sql` for initial setup

### Test Environment
- **Profile**: `test`
- **Database**: H2 in-memory database
- **DDL Mode**: `create-drop`

## Key Features

### 1. Validation
- Bean Validation annotations on all entities
- Custom validation for business rules
- Database constraints for data integrity

### 2. Auditing
- Automatic `created_date` and `updated_date` tracking
- Status history for students
- Follow-up tracking for leads

### 3. Business Logic
- Batch capacity management
- Lead conversion workflow
- Student enrollment and graduation
- Placement tracking and statistics

### 4. Security
- BCrypt password hashing
- Account locking mechanisms
- Role-based access control preparation

## Usage Examples

### Creating a Course and Batch
```java
Course course = new Course("Java Programming", "Comprehensive Java course", 6, new BigDecimal("50000"));
Batch batch = new Batch("Java Batch 2024-01", course, LocalDate.now().plusDays(30), 25);
course.addBatch(batch);
```

### Converting Lead to Student
```java
Lead lead = new Lead("John", "Doe", "john@email.com", "1234567890", "Java", "Website");
lead.convertToStudent();
Student student = new Student("STU2024001", "John", "Doe", "john@email.com", "1234567890", LocalDate.now());
lead.addStudent(student);
```

### Recording a Placement
```java
Placement placement = new Placement(student, company, "Software Developer", new BigDecimal("60000"), LocalDate.now());
placement.setJobType(Placement.JobType.FULL_TIME);
placement.setEmploymentType(Placement.EmploymentType.PERMANENT);
student.addPlacement(placement);
```

## Testing

All entities are thoroughly tested with:
- Entity mapping validation
- Relationship integrity
- Constraint enforcement
- Business logic verification

Run tests with: `mvn test -Dtest=EntityMappingTest`