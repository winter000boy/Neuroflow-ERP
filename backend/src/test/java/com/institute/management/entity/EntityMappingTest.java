package com.institute.management.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EntityMappingTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testCourseEntityMapping() {
        // Given
        Course course = new Course();
        course.setName("Java Programming");
        course.setDescription("Comprehensive Java programming course");
        course.setDurationMonths(6);
        course.setFees(new BigDecimal("50000.00"));
        course.setStatus(Course.CourseStatus.ACTIVE);

        // When
        Course savedCourse = entityManager.persistAndFlush(course);

        // Then
        assertThat(savedCourse.getId()).isNotNull();
        assertThat(savedCourse.getName()).isEqualTo("Java Programming");
        assertThat(savedCourse.getDurationMonths()).isEqualTo(6);
        assertThat(savedCourse.getFees()).isEqualByComparingTo(new BigDecimal("50000.00"));
        assertThat(savedCourse.getStatus()).isEqualTo(Course.CourseStatus.ACTIVE);
        assertThat(savedCourse.getCreatedDate()).isNotNull();
    }

    @Test
    void testEmployeeEntityMapping() {
        // Given
        Employee employee = new Employee();
        employee.setEmployeeCode("EMP001");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@institute.com");
        employee.setPhone("1234567890");
        employee.setDepartment("IT");
        employee.setRole(Employee.EmployeeRole.ADMIN);
        employee.setHireDate(LocalDate.now());
        employee.setStatus(Employee.EmployeeStatus.ACTIVE);

        // When
        Employee savedEmployee = entityManager.persistAndFlush(employee);

        // Then
        assertThat(savedEmployee.getId()).isNotNull();
        assertThat(savedEmployee.getEmployeeCode()).isEqualTo("EMP001");
        assertThat(savedEmployee.getFullName()).isEqualTo("John Doe");
        assertThat(savedEmployee.getRole()).isEqualTo(Employee.EmployeeRole.ADMIN);
        assertThat(savedEmployee.getStatus()).isEqualTo(Employee.EmployeeStatus.ACTIVE);
        assertThat(savedEmployee.getCreatedDate()).isNotNull();
    }

    @Test
    void testCompanyEntityMapping() {
        // Given
        Company company = new Company();
        company.setName("Tech Solutions Inc");
        company.setIndustry("Technology");
        company.setContactPerson("Jane Smith");
        company.setEmail("contact@techsolutions.com");
        company.setPhone("9876543210");
        company.setAddress("123 Tech Street, Silicon Valley");
        company.setPartnershipDate(LocalDate.now());
        company.setStatus(Company.CompanyStatus.ACTIVE);

        // When
        Company savedCompany = entityManager.persistAndFlush(company);

        // Then
        assertThat(savedCompany.getId()).isNotNull();
        assertThat(savedCompany.getName()).isEqualTo("Tech Solutions Inc");
        assertThat(savedCompany.getIndustry()).isEqualTo("Technology");
        assertThat(savedCompany.getStatus()).isEqualTo(Company.CompanyStatus.ACTIVE);
        assertThat(savedCompany.getCreatedDate()).isNotNull();
    }

    @Test
    void testBatchEntityMapping() {
        // Given - First create a course
        Course course = new Course();
        course.setName("Python Programming");
        course.setDurationMonths(4);
        course.setFees(new BigDecimal("40000.00"));
        Course savedCourse = entityManager.persistAndFlush(course);

        // Create batch
        Batch batch = new Batch();
        batch.setName("Python Batch 2024-01");
        batch.setCourse(savedCourse);
        batch.setStartDate(LocalDate.now().plusDays(30));
        batch.setCapacity(25);
        batch.setStatus(Batch.BatchStatus.PLANNED);

        // When
        Batch savedBatch = entityManager.persistAndFlush(batch);

        // Then
        assertThat(savedBatch.getId()).isNotNull();
        assertThat(savedBatch.getName()).isEqualTo("Python Batch 2024-01");
        assertThat(savedBatch.getCourse().getId()).isEqualTo(savedCourse.getId());
        assertThat(savedBatch.getCapacity()).isEqualTo(25);
        assertThat(savedBatch.getCurrentEnrollment()).isEqualTo(0);
        assertThat(savedBatch.hasAvailableSlots()).isTrue();
        assertThat(savedBatch.getAvailableSlots()).isEqualTo(25);
        assertThat(savedBatch.getEndDate()).isNotNull(); // Should be calculated by @PrePersist
    }

    @Test
    void testLeadEntityMapping() {
        // Given - First create an employee (counsellor)
        Employee counsellor = new Employee();
        counsellor.setEmployeeCode("COUN001");
        counsellor.setFirstName("Alice");
        counsellor.setLastName("Johnson");
        counsellor.setEmail("alice.johnson@institute.com");
        counsellor.setRole(Employee.EmployeeRole.COUNSELLOR);
        counsellor.setHireDate(LocalDate.now());
        Employee savedCounsellor = entityManager.persistAndFlush(counsellor);

        // Create lead
        Lead lead = new Lead();
        lead.setFirstName("Bob");
        lead.setLastName("Wilson");
        lead.setEmail("bob.wilson@email.com");
        lead.setPhone("5555555555");
        lead.setCourseInterest("Java Programming");
        lead.setSource("Website");
        lead.setAssignedCounsellor(savedCounsellor);
        lead.setStatus(Lead.LeadStatus.NEW);

        // When
        Lead savedLead = entityManager.persistAndFlush(lead);

        // Then
        assertThat(savedLead.getId()).isNotNull();
        assertThat(savedLead.getFullName()).isEqualTo("Bob Wilson");
        assertThat(savedLead.getStatus()).isEqualTo(Lead.LeadStatus.NEW);
        assertThat(savedLead.getAssignedCounsellor().getId()).isEqualTo(savedCounsellor.getId());
        assertThat(savedLead.isConverted()).isFalse();
        assertThat(savedLead.getCreatedDate()).isNotNull();
    }

    @Test
    void testStudentEntityMapping() {
        // Given - Create course and batch first
        Course course = new Course();
        course.setName("Data Science");
        course.setDurationMonths(8);
        course.setFees(new BigDecimal("75000.00"));
        Course savedCourse = entityManager.persistAndFlush(course);

        Batch batch = new Batch();
        batch.setName("DS Batch 2024-01");
        batch.setCourse(savedCourse);
        batch.setStartDate(LocalDate.now().plusDays(15));
        batch.setCapacity(20);
        Batch savedBatch = entityManager.persistAndFlush(batch);

        // Create student
        Student student = new Student();
        student.setEnrollmentNumber("STU2024001");
        student.setFirstName("Charlie");
        student.setLastName("Brown");
        student.setEmail("charlie.brown@email.com");
        student.setPhone("7777777777");
        student.setDateOfBirth(LocalDate.of(1995, 5, 15));
        student.setAddress("456 Student Lane, Education City");
        student.setBatch(savedBatch);
        student.setEnrollmentDate(LocalDate.now());
        student.setStatus(Student.StudentStatus.ACTIVE);

        // When
        Student savedStudent = entityManager.persistAndFlush(student);

        // Then
        assertThat(savedStudent.getId()).isNotNull();
        assertThat(savedStudent.getEnrollmentNumber()).isEqualTo("STU2024001");
        assertThat(savedStudent.getFullName()).isEqualTo("Charlie Brown");
        assertThat(savedStudent.getBatch().getId()).isEqualTo(savedBatch.getId());
        assertThat(savedStudent.isActive()).isTrue();
        assertThat(savedStudent.isGraduated()).isFalse();
        assertThat(savedStudent.getStatusHistory()).isNotNull(); // Status history is initialized
    }

    @Test
    void testPlacementEntityMapping() {
        // Given - Create all required entities first
        Course course = new Course();
        course.setName("Web Development");
        course.setDurationMonths(6);
        course.setFees(new BigDecimal("60000.00"));
        Course savedCourse = entityManager.persistAndFlush(course);

        Batch batch = new Batch();
        batch.setName("Web Dev Batch 2024-01");
        batch.setCourse(savedCourse);
        batch.setStartDate(LocalDate.now().minusMonths(6));
        batch.setCapacity(15);
        Batch savedBatch = entityManager.persistAndFlush(batch);

        Student student = new Student();
        student.setEnrollmentNumber("STU2024002");
        student.setFirstName("Diana");
        student.setLastName("Prince");
        student.setEmail("diana.prince@email.com");
        student.setPhone("8888888888");
        student.setBatch(savedBatch);
        student.setEnrollmentDate(LocalDate.now().minusMonths(6));
        Student savedStudent = entityManager.persistAndFlush(student);

        Company company = new Company();
        company.setName("Web Innovations Ltd");
        company.setIndustry("Web Development");
        company.setContactPerson("Steve Rogers");
        company.setEmail("hr@webinnovations.com");
        Company savedCompany = entityManager.persistAndFlush(company);

        // Create placement
        Placement placement = new Placement();
        placement.setStudent(savedStudent);
        placement.setCompany(savedCompany);
        placement.setPosition("Junior Web Developer");
        placement.setSalary(new BigDecimal("45000.00"));
        placement.setPlacementDate(LocalDate.now());
        placement.setJobType(Placement.JobType.FULL_TIME);
        placement.setEmploymentType(Placement.EmploymentType.PERMANENT);
        placement.setWorkLocation("Remote");
        placement.setProbationPeriodMonths(6);
        placement.setJoiningDate(LocalDate.now().plusDays(15));
        placement.setStatus(Placement.PlacementStatus.PLACED);

        // When
        Placement savedPlacement = entityManager.persistAndFlush(placement);

        // Then
        assertThat(savedPlacement.getId()).isNotNull();
        assertThat(savedPlacement.getStudent().getId()).isEqualTo(savedStudent.getId());
        assertThat(savedPlacement.getCompany().getId()).isEqualTo(savedCompany.getId());
        assertThat(savedPlacement.getPosition()).isEqualTo("Junior Web Developer");
        assertThat(savedPlacement.getSalary()).isEqualByComparingTo(new BigDecimal("45000.00"));
        assertThat(savedPlacement.isActive()).isTrue();
        assertThat(savedPlacement.isInProbation()).isTrue();
        assertThat(savedPlacement.getTenureInMonths()).isEqualTo(0);
    }

    @Test
    void testUserEntityMapping() {
        // Given - Create employee first
        Employee employee = new Employee();
        employee.setEmployeeCode("ADM001");
        employee.setFirstName("Admin");
        employee.setLastName("User");
        employee.setEmail("admin@institute.com");
        employee.setRole(Employee.EmployeeRole.ADMIN);
        employee.setHireDate(LocalDate.now());
        Employee savedEmployee = entityManager.persistAndFlush(employee);

        // Create user
        User user = new User();
        user.setUsername("admin");
        user.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"); // BCrypt hash for "password"
        user.setEmployee(savedEmployee);
        user.setStatus(User.UserStatus.ACTIVE);

        // When
        User savedUser = entityManager.persistAndFlush(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("admin");
        assertThat(savedUser.getEmployee().getId()).isEqualTo(savedEmployee.getId());
        assertThat(savedUser.getStatus()).isEqualTo(User.UserStatus.ACTIVE);
        assertThat(savedUser.getFailedLoginAttempts()).isEqualTo(0);
        assertThat(savedUser.isAccountLocked()).isFalse();
        assertThat(savedUser.getPasswordChangedDate()).isNotNull();
    }
}