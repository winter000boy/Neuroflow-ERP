package com.institute.management.repository;

import com.institute.management.entity.Course;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SimpleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void testSimpleCourseRepository() {
        // Create a simple course
        Course course = new Course();
        course.setName("Test Course");
        course.setDescription("Test Description");
        course.setDurationMonths(6);
        course.setFees(new BigDecimal("10000"));
        
        // Save and flush
        Course savedCourse = entityManager.persistAndFlush(course);
        
        // Test repository
        assertThat(courseRepository.findById(savedCourse.getId())).isPresent();
        assertThat(courseRepository.count()).isEqualTo(1);
    }
}