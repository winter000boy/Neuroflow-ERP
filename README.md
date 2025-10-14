# Educational Institute Management System

A comprehensive web application for managing educational institute operations including leads, students, batches, employees, and placements.

## Architecture

- **Backend**: Spring Boot 3.x with Java 17
- **Frontend**: Angular 17+ with Angular Material
- **Database**: PostgreSQL
- **Authentication**: JWT-based with role-based access control

## Project Structure

```
├── backend/                 # Spring Boot application
│   ├── src/main/java/
│   │   └── com/institute/management/
│   │       ├── config/      # Configuration classes
│   │       ├── controller/  # REST Controllers
│   │       ├── service/     # Business Logic Services
│   │       ├── repository/  # Data Access Repositories
│   │       ├── entity/      # JPA Entities
│   │       ├── dto/         # Data Transfer Objects
│   │       ├── security/    # Security Components
│   │       └── exception/   # Exception Handling
│   └── src/main/resources/
│       ├── application.yml  # Main configuration
│       ├── application-dev.yml
│       └── application-prod.yml
├── frontend/                # Angular application
│   └── src/app/
│       ├── core/           # Singleton services, guards, interceptors
│       ├── shared/         # Reusable components, pipes, directives
│       ├── features/       # Lazy-loaded business modules
│       ├── layouts/        # Application layouts
│       └── environments/   # Environment configurations
└── .kiro/specs/           # Project specifications and requirements
```

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- PostgreSQL 12+
- Maven 3.6+
- Angular CLI 17+

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Configure PostgreSQL database:
   - Create database: `institute_management`
   - Update connection details in `application.yml`

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Access API documentation:
   - Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
   - API Docs: http://localhost:8080/api/v1/api-docs

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   ng serve
   ```

4. Access the application:
   - URL: http://localhost:4200

## Development

### Backend Development

- **Profiles**: Use `dev` profile for development, `prod` for production
- **Database**: Auto-creates schema in dev mode, validates in prod
- **Testing**: Run tests with `mvn test`

### Frontend Development

- **Development**: `ng serve` for hot reload
- **Build**: `ng build` for production build
- **Testing**: `ng test` for unit tests, `ng e2e` for E2E tests
- **Linting**: `ng lint` for code quality

## Features

- **Authentication & Authorization**: JWT-based with role-based access control
- **Lead Management**: Track and convert potential students
- **Student Management**: Enrollment and batch assignment
- **Batch Management**: Class organization and capacity management
- **Employee Management**: Staff records and role assignments
- **Placement Management**: Job placement tracking and company relationships
- **Reporting**: Comprehensive analytics and reporting

## API Documentation

The backend provides comprehensive API documentation through OpenAPI/Swagger:
- Interactive documentation available at `/swagger-ui.html`
- JSON specification at `/api-docs`

## Security

- JWT tokens with 15-minute access tokens and 7-day refresh tokens
- Role-based access control (ADMIN, COUNSELLOR, FACULTY, PLACEMENT_OFFICER, OPERATIONS)
- BCrypt password encryption
- CORS configuration for frontend integration

## Contributing

1. Follow the established project structure
2. Implement comprehensive tests for new features
3. Update documentation for API changes
4. Follow coding standards and best practices