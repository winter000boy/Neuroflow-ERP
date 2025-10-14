# Repository Layer

This package contains Spring Data JPA repository interfaces for the Educational Institute Management System.

## Repositories

### Core Entity Repositories

1. **LeadRepository** - Manages lead data with advanced filtering and search capabilities
   - Find by status, source, course interest, assigned counsellor
   - Complex search with multiple filters
   - Statistics and conversion rate queries
   - Follow-up tracking queries

2. **StudentRepository** - Handles student enrollment and academic records
   - Find by status, batch, enrollment details
   - Academic progress tracking
   - Placement status queries
   - Enrollment statistics and trends

3. **BatchRepository** - Manages batch scheduling and capacity
   - Find by status, course, instructor
   - Capacity and utilization queries
   - Revenue calculations
   - Scheduling conflict detection

4. **CourseRepository** - Course catalog and program management
   - Find by status, duration, fees
   - Popularity and enrollment statistics
   - Revenue analysis by course
   - Fee range distributions

5. **EmployeeRepository** - Staff and faculty management
   - Find by role, department, status
   - Performance tracking for counsellors and faculty
   - Workload distribution queries
   - Hiring trends and statistics

6. **PlacementRepository** - Job placement tracking
   - Find by status, company, job type
   - Salary statistics and analysis
   - Placement rate calculations
   - Company performance metrics

7. **CompanyRepository** - Partner company management
   - Find by status, industry
   - Partnership tracking
   - Hiring activity analysis
   - Company performance statistics

8. **UserRepository** - Authentication and user management
   - Find by username, employee, status
   - Security and login tracking
   - Account health monitoring
   - Password management queries

## Key Features

### Advanced Filtering
All repositories support complex filtering with multiple criteria using custom JPQL queries.

### Statistics and Analytics
Comprehensive statistical queries for reporting and dashboard functionality.

### Pagination Support
All list queries support Spring Data pagination for efficient data handling.

### Custom Query Methods
Each repository includes both derived query methods and custom JPQL queries for complex operations.

### Test Configuration
Repository tests use H2 in-memory database with automatic schema creation for fast, isolated testing.

## Usage Example

```java
@Service
public class LeadService {
    
    @Autowired
    private LeadRepository leadRepository;
    
    public Page<Lead> searchLeads(LeadStatus status, String source, 
                                 String searchTerm, Pageable pageable) {
        return leadRepository.findLeadsWithFilters(
            status, source, null, null, searchTerm, pageable);
    }
    
    public long getConversionRate(Employee counsellor) {
        long total = leadRepository.countByCounsellorAndStatus(counsellor, null);
        long converted = leadRepository.countConvertedLeadsByCounsellor(counsellor);
        return total > 0 ? (converted * 100) / total : 0;
    }
}
```

## Testing

Repository tests are located in `src/test/java/com/institute/management/repository/` and use:
- `@DataJpaTest` for repository layer testing
- `@ActiveProfiles("test")` for test-specific configuration
- H2 in-memory database for fast, isolated tests
- TestEntityManager for test data setup

Run repository tests with:
```bash
mvn test -Dtest=*RepositoryTest
```