# Requirements Document

## Introduction

The Educational Institute Management System is a comprehensive web application designed to streamline the core operations of an educational institute. The system will manage leads, students, batches, employees, courses, and placements through a modern full-stack architecture using Spring Boot for the backend and Angular for the frontend. The system will provide role-based access control, secure authentication, and comprehensive reporting capabilities to support administrative and operational needs.

## Requirements

### Requirement 1

**User Story:** As an institute administrator, I want a secure authentication system with role-based access control, so that different users can access only the features appropriate to their role.

#### Acceptance Criteria

1. WHEN a user submits valid credentials THEN the system SHALL return an access token (15 minutes) and refresh token (7 days)
2. WHEN an access token expires THEN the system SHALL automatically refresh it using the refresh token
3. WHEN a user has ADMIN role THEN the system SHALL grant full access to all modules
4. WHEN a user has COUNSELLOR role THEN the system SHALL grant CRUD access only to Leads and Students modules
5. WHEN a user has FACULTY role THEN the system SHALL grant view-only access to Students and Batches modules
6. WHEN a user has PLACEMENT_OFFICER role THEN the system SHALL grant CRUD access only to Placements and Companies modules
7. WHEN a user has OPERATIONS role THEN the system SHALL grant CRUD access only to Batches and Courses modules
8. WHEN storing passwords THEN the system SHALL use BCrypt encryption

### Requirement 2

**User Story:** As a counsellor, I want to manage leads effectively, so that I can track potential students and convert them to enrollments.

#### Acceptance Criteria

1. WHEN creating a lead THEN the system SHALL capture contact information, course interest, and source
2. WHEN viewing leads THEN the system SHALL support pagination, sorting, and filtering by status
3. WHEN converting a lead THEN the system SHALL create a student record and update lead status
4. WHEN adding follow-up THEN the system SHALL record date, notes, and next action
5. WHEN a lead status changes THEN the system SHALL maintain an audit trail
6. IF user role is not COUNSELLOR or ADMIN THEN the system SHALL deny access to lead management

### Requirement 3

**User Story:** As an operations manager, I want to manage student enrollments and batch assignments, so that I can organize classes efficiently.

#### Acceptance Criteria

1. WHEN enrolling a student THEN the system SHALL generate a unique enrollment number
2. WHEN assigning to batch THEN the system SHALL verify batch capacity constraints
3. WHEN viewing students THEN the system SHALL support filtering by batch, status, and enrollment date
4. WHEN updating student status THEN the system SHALL track status history
5. WHEN a batch reaches capacity THEN the system SHALL prevent new assignments
6. IF user role lacks student access THEN the system SHALL deny student operations

### Requirement 4

**User Story:** As an operations manager, I want to manage batches and courses, so that I can schedule and organize educational programs effectively.

#### Acceptance Criteria

1. WHEN creating a batch THEN the system SHALL associate it with a course and set capacity limits
2. WHEN viewing batch details THEN the system SHALL show enrolled students and available slots
3. WHEN updating batch capacity THEN the system SHALL validate against current enrollments
4. WHEN managing courses THEN the system SHALL maintain course details, duration, and fees
5. WHEN deleting a batch THEN the system SHALL prevent deletion if students are enrolled
6. IF user role is not OPERATIONS or ADMIN THEN the system SHALL deny batch/course management

### Requirement 5

**User Story:** As an HR manager, I want to manage employee information, so that I can maintain staff records and role assignments.

#### Acceptance Criteria

1. WHEN adding an employee THEN the system SHALL capture personal details, role, and department
2. WHEN viewing employees THEN the system SHALL support filtering by role and department
3. WHEN updating employee role THEN the system SHALL validate role permissions
4. WHEN deactivating an employee THEN the system SHALL maintain historical records
5. IF user role is not ADMIN THEN the system SHALL provide read-only access to employee data

### Requirement 6

**User Story:** As a placement officer, I want to manage placement records and company relationships, so that I can track student job placements effectively.

#### Acceptance Criteria

1. WHEN recording a placement THEN the system SHALL link student, company, and placement details
2. WHEN managing companies THEN the system SHALL maintain company profiles and contact information
3. WHEN viewing placements THEN the system SHALL support filtering by student, company, and date range
4. WHEN generating placement statistics THEN the system SHALL calculate placement rates and salary ranges
5. IF user role is not PLACEMENT_OFFICER or ADMIN THEN the system SHALL deny placement management

### Requirement 7

**User Story:** As an institute administrator, I want comprehensive reporting capabilities, so that I can make data-driven decisions about institute operations.

#### Acceptance Criteria

1. WHEN generating revenue reports THEN the system SHALL calculate income by course, batch, and time period
2. WHEN viewing enrollment reports THEN the system SHALL show trends and conversion rates
3. WHEN accessing placement reports THEN the system SHALL display placement statistics and company partnerships
4. WHEN reviewing lead conversion THEN the system SHALL show conversion rates by source and counsellor
5. WHEN analyzing batch utilization THEN the system SHALL show capacity usage and efficiency metrics
6. WHEN evaluating faculty performance THEN the system SHALL provide relevant metrics and feedback data

### Requirement 8

**User Story:** As a system user, I want a responsive and intuitive user interface, so that I can efficiently perform my tasks across different devices.

#### Acceptance Criteria

1. WHEN accessing the application THEN the system SHALL provide a Material Design compliant interface
2. WHEN using forms THEN the system SHALL provide real-time validation and clear error messages
3. WHEN viewing data tables THEN the system SHALL support sorting, filtering, and pagination
4. WHEN using the application on mobile devices THEN the system SHALL maintain full functionality
5. WHEN navigating between modules THEN the system SHALL provide consistent layout and navigation patterns

### Requirement 9

**User Story:** As a system administrator, I want robust data persistence and API documentation, so that the system is maintainable and integrable.

#### Acceptance Criteria

1. WHEN storing data THEN the system SHALL use PostgreSQL with proper constraints and relationships
2. WHEN accessing APIs THEN the system SHALL provide comprehensive OpenAPI/Swagger documentation
3. WHEN handling errors THEN the system SHALL return consistent JSON error responses
4. WHEN processing requests THEN the system SHALL follow RESTful conventions with proper HTTP status codes
5. WHEN managing database operations THEN the system SHALL use JPA entities with proper mappings and validations