package com.institute.management.service;

import com.institute.management.dto.*;
import com.institute.management.entity.*;
import com.institute.management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportsService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private LeadRepository leadRepository;
    
    @Autowired
    private BatchRepository batchRepository;
    
    @Autowired
    private PlacementRepository placementRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    /**
     * Generate revenue reports - Only ADMIN can access revenue reports
     */
    @PreAuthorize("hasRole('ADMIN')")
    public RevenueReportDTO generateRevenueReport(LocalDate startDate, LocalDate endDate) {
        // Get revenue by course
        List<Object[]> courseRevenueData = courseRepository.getCourseRevenueReport();
        Map<String, BigDecimal> revenueByCourse = courseRevenueData.stream()
            .collect(Collectors.toMap(
                data -> ((Course) data[0]).getName(),
                data -> data[1] != null ? (BigDecimal) data[1] : BigDecimal.ZERO,
                (existing, replacement) -> existing
            ));
        
        // Calculate total revenue
        BigDecimal totalRevenue = revenueByCourse.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Get enrollments in date range for revenue calculation
        List<Student> enrollmentsInRange = studentRepository.findByEnrollmentDateBetween(startDate, endDate);
        
        // Calculate revenue by month
        Map<String, BigDecimal> revenueByMonth = new LinkedHashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        for (Student student : enrollmentsInRange) {
            String monthKey = student.getEnrollmentDate().format(monthFormatter);
            BigDecimal courseFee = student.getBatch() != null ? student.getBatch().getCourse().getFees() : BigDecimal.ZERO;
            revenueByMonth.merge(monthKey, courseFee, BigDecimal::add);
        }
        
        // Calculate revenue by batch
        Map<String, BigDecimal> revenueByBatch = new HashMap<>();
        List<Batch> allBatches = batchRepository.findAll();
        for (Batch batch : allBatches) {
            BigDecimal batchRevenue = BigDecimal.valueOf(batch.getCurrentEnrollment())
                .multiply(batch.getCourse().getFees());
            revenueByBatch.put(batch.getName(), batchRevenue);
        }
        
        // Calculate average revenue per student
        BigDecimal averageRevenuePerStudent = enrollmentsInRange.isEmpty() ? 
            BigDecimal.ZERO : 
            totalRevenue.divide(BigDecimal.valueOf(enrollmentsInRange.size()), 2, RoundingMode.HALF_UP);
        
        // Calculate growth rate (comparing with previous period)
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        LocalDate previousStartDate = startDate.minusDays(daysBetween);
        List<Student> previousEnrollments = studentRepository.findByEnrollmentDateBetween(previousStartDate, startDate);
        BigDecimal previousRevenue = previousEnrollments.stream()
            .filter(s -> s.getBatch() != null)
            .map(s -> s.getBatch().getCourse().getFees())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal growthRate = previousRevenue.compareTo(BigDecimal.ZERO) == 0 ? 
            BigDecimal.ZERO : 
            totalRevenue.subtract(previousRevenue)
                .divide(previousRevenue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        
        RevenueReportDTO report = new RevenueReportDTO();
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setTotalRevenue(totalRevenue);
        report.setProjectedRevenue(totalRevenue.multiply(BigDecimal.valueOf(1.1)));
        report.setRevenueByMonth(revenueByMonth);
        report.setRevenueByCourse(revenueByCourse);
        report.setRevenueByBatch(revenueByBatch);
        report.setAverageRevenuePerStudent(averageRevenuePerStudent);
        report.setTotalEnrollments(enrollmentsInRange.size());
        report.setGrowthRate(growthRate);
        report.setGeneratedAt(LocalDate.now());
        
        return report;
    }
    
    /**
     * Generate enrollment reports - ADMIN and OPERATIONS can access enrollment reports
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
    public Map<String, Object> generateEnrollmentReport(LocalDate startDate, LocalDate endDate) {
        // Get enrollments in date range
        List<Student> enrollmentsInRange = studentRepository.findByEnrollmentDateBetween(startDate, endDate);
        
        // Get enrollment trends
        List<Object[]> trendData = studentRepository.getEnrollmentTrends(startDate, endDate);
        Map<String, Integer> enrollmentTrends = trendData.stream()
            .collect(Collectors.toMap(
                data -> ((LocalDate) data[0]).toString(),
                data -> ((Long) data[1]).intValue(),
                (existing, replacement) -> existing,
                LinkedHashMap::new
            ));
        
        // Get enrollments by course
        List<Object[]> courseData = studentRepository.countByCourse();
        Map<String, Integer> enrollmentsByCourse = courseData.stream()
            .collect(Collectors.toMap(
                data -> ((Course) data[0]).getName(),
                data -> ((Long) data[1]).intValue()
            ));
        
        // Get enrollments by batch
        List<Object[]> batchData = studentRepository.countByBatch();
        Map<String, Integer> enrollmentsByBatch = batchData.stream()
            .collect(Collectors.toMap(
                data -> ((Batch) data[0]).getName(),
                data -> ((Long) data[1]).intValue()
            ));
        
        // Get enrollments by status
        Map<String, Integer> enrollmentsByStatus = new HashMap<>();
        for (Student.StudentStatus status : Student.StudentStatus.values()) {
            long count = studentRepository.countByStatus(status);
            enrollmentsByStatus.put(status.name(), (int) count);
        }
        
        // Calculate metrics
        int totalEnrollments = enrollmentsInRange.size();
        int activeStudents = (int) studentRepository.countByStatus(Student.StudentStatus.ACTIVE);
        int graduatedStudents = (int) studentRepository.countByStatus(Student.StudentStatus.GRADUATED);
        int droppedStudents = (int) studentRepository.countByStatus(Student.StudentStatus.DROPPED_OUT);
        
        // Calculate average enrollments per month
        long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate) + 1;
        double averageEnrollmentsPerMonth = monthsBetween > 0 ? 
            (double) totalEnrollments / monthsBetween : 0.0;
        
        // Calculate growth rate
        long daysBetween2 = ChronoUnit.DAYS.between(startDate, endDate);
        LocalDate previousStartDate2 = startDate.minusDays(daysBetween2);
        List<Student> previousEnrollments = studentRepository.findByEnrollmentDateBetween(previousStartDate2, startDate);
        double growthRate = previousEnrollments.isEmpty() ? 0.0 : 
            ((double) (totalEnrollments - previousEnrollments.size()) / previousEnrollments.size()) * 100;
        
        // Calculate retention rate (active + graduated / total)
        int totalStudents = activeStudents + graduatedStudents + droppedStudents;
        double retentionRate = totalStudents > 0 ? 
            ((double) (activeStudents + graduatedStudents) / totalStudents) * 100 : 0.0;
        
        // Calculate completion rate (graduated / (graduated + dropped))
        int completedOrDropped = graduatedStudents + droppedStudents;
        double completionRate = completedOrDropped > 0 ? 
            ((double) graduatedStudents / completedOrDropped) * 100 : 0.0;
        
        return EnrollmentReportDTO.builder()
            .startDate(startDate)
            .endDate(endDate)
            .totalEnrollments(totalEnrollments)
            .activeStudents(activeStudents)
            .graduatedStudents(graduatedStudents)
            .droppedStudents(droppedStudents)
            .enrollmentTrends(enrollmentTrends)
            .enrollmentsByCourse(enrollmentsByCourse)
            .enrollmentsByBatch(enrollmentsByBatch)
            .enrollmentsByStatus(enrollmentsByStatus)
            .averageEnrollmentsPerMonth(averageEnrollmentsPerMonth)
            .growthRate(growthRate)
            .retentionRate(retentionRate)
            .completionRate(completionRate)
            .generatedAt(LocalDate.now())
            .build();
    }
    
    /**
     * Generate placement reports - ADMIN and PLACEMENT_OFFICER can access placement reports
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public PlacementReportDTO generatePlacementReport(LocalDate startDate, LocalDate endDate) {
        // Get placements in date range
        List<Placement> placementsInRange = placementRepository.findByPlacementDateBetween(startDate, endDate);
        
        // Get placements by company
        List<Object[]> companyData = placementRepository.countByCompany();
        Map<String, Integer> placementsByCompany = companyData.stream()
            .collect(Collectors.toMap(
                data -> ((Company) data[0]).getName(),
                data -> ((Long) data[1]).intValue()
            ));
        
        // Get placement rate by course
        List<Object[]> courseRateData = placementRepository.getPlacementRateByCourse();
        Map<String, Integer> placementsByCourse = courseRateData.stream()
            .collect(Collectors.toMap(
                data -> (String) data[0], // course name
                data -> ((Long) data[2]).intValue() // placed students
            ));
        
        // Get placements by job type
        List<Object[]> jobTypeData = placementRepository.countByJobType();
        Map<String, Integer> placementsByJobType = jobTypeData.stream()
            .collect(Collectors.toMap(
                data -> ((Placement.JobType) data[0]).name(),
                data -> ((Long) data[1]).intValue()
            ));
        
        // Get placements by employment type
        List<Object[]> employmentTypeData = placementRepository.countByEmploymentType();
        Map<String, Integer> placementsByEmploymentType = employmentTypeData.stream()
            .collect(Collectors.toMap(
                data -> ((Placement.EmploymentType) data[0]).name(),
                data -> ((Long) data[1]).intValue()
            ));
        
        // Get placement trends
        List<Object[]> trendData = placementRepository.getMonthlyPlacementTrends(startDate.minusYears(1));
        Map<String, Integer> placementTrends = trendData.stream()
            .collect(Collectors.toMap(
                data -> data[0] + "-" + String.format("%02d", data[1]),
                data -> ((Long) data[2]).intValue(),
                (existing, replacement) -> existing,
                LinkedHashMap::new
            ));
        
        // Calculate salary statistics
        BigDecimal averageSalary = placementRepository.getAverageSalary();
        List<Object[]> salaryRange = placementRepository.getSalaryRange();
        BigDecimal minSalary = salaryRange.isEmpty() ? BigDecimal.ZERO : (BigDecimal) salaryRange.get(0)[0];
        BigDecimal maxSalary = salaryRange.isEmpty() ? BigDecimal.ZERO : (BigDecimal) salaryRange.get(0)[1];
        
        // Get salary range distribution
        List<Object[]> salaryDistData = placementRepository.getSalaryRangeDistribution();
        Map<String, BigDecimal> salaryRangeDistribution = new HashMap<>();
        if (!salaryDistData.isEmpty()) {
            Object[] data = salaryDistData.get(0);
            salaryRangeDistribution.put("Low (< 3L)", BigDecimal.valueOf((Long) data[0]));
            salaryRangeDistribution.put("Medium (3L-6L)", BigDecimal.valueOf((Long) data[1]));
            salaryRangeDistribution.put("High (> 6L)", BigDecimal.valueOf((Long) data[2]));
        }
        
        // Get average salary by company
        List<Object[]> companySalaryData = placementRepository.getAverageSalaryByCompany();
        Map<String, BigDecimal> averageSalaryByCompany = companySalaryData.stream()
            .collect(Collectors.toMap(
                data -> ((Company) data[0]).getName(),
                data -> (BigDecimal) data[1]
            ));
        
        // Get average salary by course
        List<Object[]> courseSalaryData = placementRepository.getAverageSalaryByCourse();
        Map<String, BigDecimal> averageSalaryByCourse = courseSalaryData.stream()
            .collect(Collectors.toMap(
                data -> (String) data[0], // course name
                data -> (BigDecimal) data[1]
            ));
        
        // Calculate placement metrics
        int totalPlacements = placementsInRange.size();
        int activePlacements = placementRepository.findActivePlacements(LocalDate.now()).size();
        long placedStudents = placementRepository.countPlacedStudents();
        long totalGraduates = placementRepository.countGraduatedStudents();
        
        double placementRate = totalGraduates > 0 ? 
            ((double) placedStudents / totalGraduates) * 100 : 0.0;
        
        // Calculate median salary (simplified - would need more complex query for true median)
        BigDecimal medianSalary = averageSalary; // Simplified for now
        
        return PlacementReportDTO.builder()
            .startDate(startDate)
            .endDate(endDate)
            .totalPlacements(totalPlacements)
            .activePlacements(activePlacements)
            .placedStudents((int) placedStudents)
            .totalGraduates((int) totalGraduates)
            .placementRate(placementRate)
            .placementsByCompany(placementsByCompany)
            .placementsByCourse(placementsByCourse)
            .placementsByJobType(placementsByJobType)
            .placementsByEmploymentType(placementsByEmploymentType)
            .placementTrends(placementTrends)
            .averageSalary(averageSalary != null ? averageSalary : BigDecimal.ZERO)
            .medianSalary(medianSalary != null ? medianSalary : BigDecimal.ZERO)
            .minSalary(minSalary != null ? minSalary : BigDecimal.ZERO)
            .maxSalary(maxSalary != null ? maxSalary : BigDecimal.ZERO)
            .salaryRangeDistribution(salaryRangeDistribution)
            .averageSalaryByCompany(averageSalaryByCompany)
            .averageSalaryByCourse(averageSalaryByCourse)
            .generatedAt(LocalDate.now())
            .build();
    }
    
    /**
     * Generate lead conversion reports - ADMIN and COUNSELLOR can access lead conversion reports
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('COUNSELLOR')")
    public LeadConversionReportDTO generateLeadConversionReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        // Get leads in date range
        List<Lead> leadsInRange = leadRepository.findByCreatedDateBetween(startDateTime, endDateTime);
        
        // Get leads by source
        List<Object[]> sourceData = leadRepository.countBySource();
        Map<String, Integer> leadsBySource = sourceData.stream()
            .collect(Collectors.toMap(
                data -> (String) data[0],
                data -> ((Long) data[1]).intValue()
            ));
        
        // Get leads by status
        Map<String, Integer> leadsByStatus = new HashMap<>();
        for (Lead.LeadStatus status : Lead.LeadStatus.values()) {
            long count = leadRepository.countByStatus(status);
            leadsByStatus.put(status.name(), (int) count);
        }
        
        // Get leads by course interest
        List<Object[]> courseInterestData = leadRepository.countByCourseInterest();
        Map<String, Integer> leadsByCourseInterest = courseInterestData.stream()
            .collect(Collectors.toMap(
                data -> (String) data[0],
                data -> ((Long) data[1]).intValue()
            ));
        
        // Calculate conversion rates by source
        Map<String, Double> conversionRateBySource = new HashMap<>();
        for (String source : leadsBySource.keySet()) {
            List<Lead> sourceLeads = leadRepository.findBySource(source);
            long convertedCount = sourceLeads.stream()
                .mapToLong(lead -> lead.getStatus() == Lead.LeadStatus.CONVERTED ? 1 : 0)
                .sum();
            double rate = sourceLeads.isEmpty() ? 0.0 : 
                ((double) convertedCount / sourceLeads.size()) * 100;
            conversionRateBySource.put(source, rate);
        }
        
        // Calculate conversion rates by counsellor
        Map<String, Double> conversionRateByCounsellor = new HashMap<>();
        List<Employee> counsellors = employeeRepository.findByRole(Employee.EmployeeRole.COUNSELLOR);
        for (Employee counsellor : counsellors) {
            List<Lead> counsellorLeads = leadRepository.findByAssignedCounsellor(counsellor);
            long convertedCount = leadRepository.countConvertedLeadsByCounsellor(counsellor);
            double rate = counsellorLeads.isEmpty() ? 0.0 : 
                ((double) convertedCount / counsellorLeads.size()) * 100;
            conversionRateByCounsellor.put(counsellor.getFirstName() + " " + counsellor.getLastName(), rate);
        }
        
        // Calculate conversion trends (monthly)
        Map<String, Integer> conversionTrends = new LinkedHashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        List<Lead> convertedLeads = leadsInRange.stream()
            .filter(lead -> lead.getStatus() == Lead.LeadStatus.CONVERTED && lead.getConvertedDate() != null)
            .collect(Collectors.toList());
        
        for (Lead lead : convertedLeads) {
            String monthKey = lead.getConvertedDate().format(monthFormatter);
            conversionTrends.merge(monthKey, 1, Integer::sum);
        }
        
        // Calculate metrics
        int totalLeads = leadsInRange.size();
        int convertedLeadsCount = (int) leadsInRange.stream()
            .mapToLong(lead -> lead.getStatus() == Lead.LeadStatus.CONVERTED ? 1 : 0)
            .sum();
        int activeLeads = (int) leadRepository.countByStatus(Lead.LeadStatus.NEW) +
                         (int) leadRepository.countByStatus(Lead.LeadStatus.CONTACTED) +
                         (int) leadRepository.countByStatus(Lead.LeadStatus.INTERESTED);
        int lostLeads = (int) leadRepository.countByStatus(Lead.LeadStatus.LOST) +
                       (int) leadRepository.countByStatus(Lead.LeadStatus.NOT_INTERESTED);
        
        double conversionRate = totalLeads > 0 ? 
            ((double) convertedLeadsCount / totalLeads) * 100 : 0.0;
        
        // Calculate average conversion time
        double averageConversionTime = convertedLeads.stream()
            .filter(lead -> lead.getCreatedDate() != null && lead.getConvertedDate() != null)
            .mapToLong(lead -> ChronoUnit.DAYS.between(lead.getCreatedDate(), lead.getConvertedDate()))
            .average()
            .orElse(0.0);
        
        // Get leads requiring follow-up
        int leadsRequiringFollowUp = leadRepository.findLeadsRequiringFollowUp(LocalDateTime.now()).size();
        
        // Follow-up activity (simplified)
        Map<String, Integer> followUpActivity = new HashMap<>();
        followUpActivity.put("Pending", leadsRequiringFollowUp);
        followUpActivity.put("Completed", totalLeads - leadsRequiringFollowUp);
        
        return LeadConversionReportDTO.builder()
            .startDate(startDate)
            .endDate(endDate)
            .totalLeads(totalLeads)
            .convertedLeads(convertedLeadsCount)
            .activeLeads(activeLeads)
            .lostLeads(lostLeads)
            .conversionRate(conversionRate)
            .leadsBySource(leadsBySource)
            .leadsByStatus(leadsByStatus)
            .leadsByCourseInterest(leadsByCourseInterest)
            .conversionRateBySource(conversionRateBySource)
            .conversionRateByCounsellor(conversionRateByCounsellor)
            .conversionTrends(conversionTrends)
            .averageConversionTime(averageConversionTime)
            .leadsRequiringFollowUp(leadsRequiringFollowUp)
            .followUpActivity(followUpActivity)
            .generatedAt(LocalDate.now())
            .build();
    }
    
    /**
     * Generate batch utilization reports - ADMIN and OPERATIONS can access batch utilization reports
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
    public BatchUtilizationReportDTO generateBatchUtilizationReport() {
        // Get all batches
        List<Batch> allBatches = batchRepository.findAll();
        
        // Get utilization by batch
        List<Object[]> utilizationData = batchRepository.getBatchUtilizationReport();
        Map<String, Double> utilizationByBatch = utilizationData.stream()
            .collect(Collectors.toMap(
                data -> ((Batch) data[0]).getName(),
                data -> (Double) data[1]
            ));
        
        // Get utilization by course
        Map<String, Double> utilizationByCourse = new HashMap<>();
        List<Object[]> courseData = batchRepository.countByCourse();
        for (Object[] data : courseData) {
            Course course = (Course) data[0];
            List<Batch> courseBatches = batchRepository.findByCourse(course);
            double avgUtilization = courseBatches.stream()
                .filter(b -> b.getCapacity() > 0)
                .mapToDouble(b -> ((double) b.getCurrentEnrollment() / b.getCapacity()) * 100)
                .average()
                .orElse(0.0);
            utilizationByCourse.put(course.getName(), avgUtilization);
        }
        
        // Get batches by status
        Map<String, Integer> batchesByStatus = new HashMap<>();
        for (Batch.BatchStatus status : Batch.BatchStatus.values()) {
            long count = batchRepository.countByStatus(status);
            batchesByStatus.put(status.name(), (int) count);
        }
        
        // Calculate utilization categories
        int underutilizedBatches = 0;
        int overutilizedBatches = 0;
        int optimallyUtilizedBatches = 0;
        
        for (Batch batch : allBatches) {
            if (batch.getCapacity() > 0) {
                double utilization = ((double) batch.getCurrentEnrollment() / batch.getCapacity()) * 100;
                if (utilization < 70) {
                    underutilizedBatches++;
                } else if (utilization > 95) {
                    overutilizedBatches++;
                } else {
                    optimallyUtilizedBatches++;
                }
            }
        }
        
        // Calculate overall metrics
        Double averageUtilization = batchRepository.getAverageUtilization();
        int totalBatches = allBatches.size();
        int activeBatches = (int) batchRepository.countByStatus(Batch.BatchStatus.ACTIVE);
        int plannedBatches = (int) batchRepository.countByStatus(Batch.BatchStatus.PLANNED);
        int completedBatches = (int) batchRepository.countByStatus(Batch.BatchStatus.COMPLETED);
        
        // Calculate capacity metrics
        int totalCapacity = allBatches.stream().mapToInt(Batch::getCapacity).sum();
        int totalEnrollment = allBatches.stream().mapToInt(Batch::getCurrentEnrollment).sum();
        double capacityEfficiency = totalCapacity > 0 ? 
            ((double) totalEnrollment / totalCapacity) * 100 : 0.0;
        
        // Utilization trends (simplified - would need historical data)
        Map<String, Object> utilizationTrends = new HashMap<>();
        utilizationTrends.put("current", averageUtilization);
        utilizationTrends.put("trend", "stable"); // Would need historical comparison
        
        return BatchUtilizationReportDTO.builder()
            .totalBatches(totalBatches)
            .activeBatches(activeBatches)
            .plannedBatches(plannedBatches)
            .completedBatches(completedBatches)
            .averageUtilization(averageUtilization != null ? averageUtilization : 0.0)
            .utilizationByBatch(utilizationByBatch)
            .utilizationByCourse(utilizationByCourse)
            .batchesByStatus(batchesByStatus)
            .underutilizedBatches(underutilizedBatches)
            .overutilizedBatches(overutilizedBatches)
            .optimallyUtilizedBatches(optimallyUtilizedBatches)
            .utilizationTrends(utilizationTrends)
            .capacityEfficiency(capacityEfficiency)
            .totalCapacity(totalCapacity)
            .totalEnrollment(totalEnrollment)
            .generatedAt(LocalDate.now())
            .build();
    }
    
    /**
     * Generate faculty performance reports - Only ADMIN can access faculty performance reports
     */
    @PreAuthorize("hasRole('ADMIN')")
    public FacultyPerformanceReportDTO generateFacultyPerformanceReport(LocalDate startDate, LocalDate endDate) {
        // Get all faculty members
        List<Employee> faculty = employeeRepository.findByRole(Employee.EmployeeRole.FACULTY);
        
        // Get batches per faculty
        List<Object[]> batchData = batchRepository.countByInstructor();
        Map<String, Integer> batchesPerFaculty = batchData.stream()
            .collect(Collectors.toMap(
                data -> {
                    Employee instructor = (Employee) data[0];
                    return instructor.getFirstName() + " " + instructor.getLastName();
                },
                data -> ((Long) data[1]).intValue()
            ));
        
        // Calculate students per faculty
        Map<String, Integer> studentsPerFaculty = new HashMap<>();
        Map<String, Double> completionRatesByFaculty = new HashMap<>();
        Map<String, Double> placementRatesByFaculty = new HashMap<>();
        
        for (Employee facultyMember : faculty) {
            String facultyName = facultyMember.getFirstName() + " " + facultyMember.getLastName();
            
            // Get batches taught by this faculty
            List<Batch> facultyBatches = batchRepository.findByInstructor(facultyMember);
            
            // Count total students
            int totalStudents = facultyBatches.stream()
                .mapToInt(Batch::getCurrentEnrollment)
                .sum();
            studentsPerFaculty.put(facultyName, totalStudents);
            
            // Calculate completion rate
            long graduatedStudents = facultyBatches.stream()
                .flatMap(batch -> batch.getStudents().stream())
                .filter(student -> student.getStatus() == Student.StudentStatus.GRADUATED)
                .count();
            
            long totalCompletedOrDropped = facultyBatches.stream()
                .flatMap(batch -> batch.getStudents().stream())
                .filter(student -> student.getStatus() == Student.StudentStatus.GRADUATED || 
                                 student.getStatus() == Student.StudentStatus.DROPPED_OUT)
                .count();
            
            double completionRate = totalCompletedOrDropped > 0 ? 
                ((double) graduatedStudents / totalCompletedOrDropped) * 100 : 0.0;
            completionRatesByFaculty.put(facultyName, completionRate);
            
            // Calculate placement rate for this faculty's students
            long placedStudents = facultyBatches.stream()
                .flatMap(batch -> batch.getStudents().stream())
                .filter(student -> student.getStatus() == Student.StudentStatus.GRADUATED)
                .filter(student -> !student.getPlacements().isEmpty() && 
                                 student.getPlacements().stream()
                                     .anyMatch(p -> p.getStatus() == Placement.PlacementStatus.PLACED))
                .count();
            
            double placementRate = graduatedStudents > 0 ? 
                ((double) placedStudents / graduatedStudents) * 100 : 0.0;
            placementRatesByFaculty.put(facultyName, placementRate);
        }
        
        // Calculate workload distribution
        Map<String, Integer> workloadDistribution = new HashMap<>();
        workloadDistribution.put("Light (1-2 batches)", 0);
        workloadDistribution.put("Medium (3-5 batches)", 0);
        workloadDistribution.put("Heavy (6+ batches)", 0);
        
        for (Integer batchCount : batchesPerFaculty.values()) {
            if (batchCount <= 2) {
                workloadDistribution.merge("Light (1-2 batches)", 1, Integer::sum);
            } else if (batchCount <= 5) {
                workloadDistribution.merge("Medium (3-5 batches)", 1, Integer::sum);
            } else {
                workloadDistribution.merge("Heavy (6+ batches)", 1, Integer::sum);
            }
        }
        
        // Calculate overall metrics
        int totalFaculty = faculty.size();
        int activeFaculty = (int) faculty.stream()
            .filter(f -> f.getStatus() == Employee.EmployeeStatus.ACTIVE)
            .count();
        
        double averageBatchesPerFaculty = batchesPerFaculty.values().stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
        
        double averageStudentsPerFaculty = studentsPerFaculty.values().stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
        
        double overallCompletionRate = completionRatesByFaculty.values().stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
        
        // Faculty metrics summary
        Map<String, Object> facultyMetrics = new HashMap<>();
        facultyMetrics.put("totalFaculty", totalFaculty);
        facultyMetrics.put("activeFaculty", activeFaculty);
        facultyMetrics.put("averageBatchesPerFaculty", averageBatchesPerFaculty);
        facultyMetrics.put("averageStudentsPerFaculty", averageStudentsPerFaculty);
        facultyMetrics.put("overallCompletionRate", overallCompletionRate);
        
        // Student satisfaction (placeholder - would need survey data)
        Map<String, Double> studentSatisfactionByFaculty = new HashMap<>();
        for (String facultyName : batchesPerFaculty.keySet()) {
            studentSatisfactionByFaculty.put(facultyName, 4.0 + Math.random()); // Placeholder 4.0-5.0 rating
        }
        
        // Performance trends (placeholder - would need historical data)
        Map<String, Object> performanceTrends = new HashMap<>();
        performanceTrends.put("completionRateTrend", "improving");
        performanceTrends.put("placementRateTrend", "stable");
        performanceTrends.put("workloadTrend", "balanced");
        
        return FacultyPerformanceReportDTO.builder()
            .startDate(startDate)
            .endDate(endDate)
            .totalFaculty(totalFaculty)
            .activeFaculty(activeFaculty)
            .facultyMetrics(facultyMetrics)
            .batchesPerFaculty(batchesPerFaculty)
            .studentsPerFaculty(studentsPerFaculty)
            .completionRatesByFaculty(completionRatesByFaculty)
            .placementRatesByFaculty(placementRatesByFaculty)
            .studentSatisfactionByFaculty(studentSatisfactionByFaculty)
            .workloadDistribution(workloadDistribution)
            .averageBatchesPerFaculty(averageBatchesPerFaculty)
            .averageStudentsPerFaculty(averageStudentsPerFaculty)
            .overallCompletionRate(overallCompletionRate)
            .performanceTrends(performanceTrends)
            .generatedAt(LocalDate.now())
            .build();
    }
    
    /**
     * Generate dashboard summary - All authenticated users can access basic dashboard data
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('FACULTY') or hasRole('COUNSELLOR') or hasRole('PLACEMENT_OFFICER')")
    public DashboardSummaryDTO generateDashboardSummary() {
        // Basic counts
        long totalStudents = studentRepository.count();
        int activeStudents = (int) studentRepository.countByStatus(Student.StudentStatus.ACTIVE);
        long totalBatches = batchRepository.count();
        int activeBatches = (int) batchRepository.countByStatus(Batch.BatchStatus.ACTIVE);
        long totalCourses = courseRepository.count();
        int activeCourses = (int) courseRepository.countByStatus(Course.CourseStatus.ACTIVE);
        long totalLeads = leadRepository.count();
        int activeLeads = (int) leadRepository.countByStatus(Lead.LeadStatus.NEW) +
                         (int) leadRepository.countByStatus(Lead.LeadStatus.CONTACTED) +
                         (int) leadRepository.countByStatus(Lead.LeadStatus.INTERESTED);
        long totalPlacements = placementRepository.count();
        int activePlacements = placementRepository.findActivePlacements(LocalDate.now()).size();
        long totalEmployees = employeeRepository.count();
        int activeEmployees = (int) employeeRepository.countByStatus(Employee.EmployeeStatus.ACTIVE);
        
        // Financial metrics
        Double totalRevenueDouble = batchRepository.getTotalRevenue();
        BigDecimal totalRevenue = totalRevenueDouble != null ? BigDecimal.valueOf(totalRevenueDouble) : BigDecimal.ZERO;
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        List<Student> monthlyEnrollments = studentRepository.findByEnrollmentDateBetween(
            currentMonth, LocalDate.now());
        BigDecimal monthlyRevenue = monthlyEnrollments.stream()
            .filter(s -> s.getBatch() != null)
            .map(s -> s.getBatch().getCourse().getFees())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate yearly revenue (last 12 months)
        LocalDate yearAgo = LocalDate.now().minusYears(1);
        List<Student> yearlyEnrollments = studentRepository.findByEnrollmentDateBetween(yearAgo, LocalDate.now());
        BigDecimal yearlyRevenue = yearlyEnrollments.stream()
            .filter(s -> s.getBatch() != null)
            .map(s -> s.getBatch().getCourse().getFees())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Performance metrics
        long placedStudents = placementRepository.countPlacedStudents();
        long graduatedStudents = placementRepository.countGraduatedStudents();
        double placementRate = graduatedStudents > 0 ? 
            ((double) placedStudents / graduatedStudents) * 100 : 0.0;
        
        long convertedLeads = leadRepository.countByStatus(Lead.LeadStatus.CONVERTED);
        double conversionRate = totalLeads > 0 ? 
            ((double) convertedLeads / totalLeads) * 100 : 0.0;
        
        Double batchUtilization = batchRepository.getAverageUtilization();
        
        // Calculate retention rate
        int droppedStudents = (int) studentRepository.countByStatus(Student.StudentStatus.DROPPED_OUT);
        int graduatedCount = (int) studentRepository.countByStatus(Student.StudentStatus.GRADUATED);
        int totalCompleted = activeStudents + graduatedCount + droppedStudents;
        double retentionRate = totalCompleted > 0 ? 
            ((double) (activeStudents + graduatedCount) / totalCompleted) * 100 : 0.0;
        
        // Recent activity (last 30 days)
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        int recentEnrollments = studentRepository.findByEnrollmentDateBetween(thirtyDaysAgo, LocalDate.now()).size();
        int recentPlacements = placementRepository.findByPlacementDateBetween(thirtyDaysAgo, LocalDate.now()).size();
        
        LocalDateTime thirtyDaysAgoDateTime = thirtyDaysAgo.atStartOfDay();
        int recentLeads = leadRepository.findByCreatedDateBetween(thirtyDaysAgoDateTime, LocalDateTime.now()).size();
        int pendingFollowUps = leadRepository.findLeadsRequiringFollowUp(LocalDateTime.now()).size();
        
        // Trends (last 6 months)
        Map<String, Integer> enrollmentTrends = new LinkedHashMap<>();
        Map<String, Integer> placementTrends = new LinkedHashMap<>();
        Map<String, BigDecimal> revenueTrends = new LinkedHashMap<>();
        
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = LocalDate.now().minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
            String monthKey = monthStart.format(monthFormatter);
            
            // Enrollment trends
            int monthEnrollments = studentRepository.findByEnrollmentDateBetween(monthStart, monthEnd).size();
            enrollmentTrends.put(monthKey, monthEnrollments);
            
            // Placement trends
            int monthPlacements = placementRepository.findByPlacementDateBetween(monthStart, monthEnd).size();
            placementTrends.put(monthKey, monthPlacements);
            
            // Revenue trends
            List<Student> monthStudents = studentRepository.findByEnrollmentDateBetween(monthStart, monthEnd);
            BigDecimal monthRevenue = monthStudents.stream()
                .filter(s -> s.getBatch() != null)
                .map(s -> s.getBatch().getCourse().getFees())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            revenueTrends.put(monthKey, monthRevenue);
        }
        
        // Alerts and notifications
        int underutilizedBatches = batchRepository.findBatchesAboveUtilization(0.7).size();
        int upcomingBatches = batchRepository.findBatchesStartingInRange(
            LocalDate.now(), LocalDate.now().plusDays(30)).size();
        
        // Role-specific data (placeholder)
        Map<String, Object> roleSpecificData = new HashMap<>();
        roleSpecificData.put("hasAccess", true);
        
        return DashboardSummaryDTO.builder()
            .totalStudents((int) totalStudents)
            .activeStudents(activeStudents)
            .totalBatches((int) totalBatches)
            .activeBatches(activeBatches)
            .totalCourses((int) totalCourses)
            .activeCourses(activeCourses)
            .totalLeads((int) totalLeads)
            .activeLeads(activeLeads)
            .totalPlacements((int) totalPlacements)
            .activePlacements(activePlacements)
            .totalEmployees((int) totalEmployees)
            .activeEmployees(activeEmployees)
            .monthlyRevenue(monthlyRevenue)
            .yearlyRevenue(yearlyRevenue)
            .projectedRevenue(yearlyRevenue.multiply(BigDecimal.valueOf(1.1)))
            .placementRate(placementRate)
            .conversionRate(conversionRate)
            .batchUtilization(batchUtilization != null ? batchUtilization : 0.0)
            .studentRetentionRate(retentionRate)
            .recentEnrollments(recentEnrollments)
            .recentPlacements(recentPlacements)
            .recentLeads(recentLeads)
            .pendingFollowUps(pendingFollowUps)
            .enrollmentTrends(enrollmentTrends)
            .placementTrends(placementTrends)
            .revenueTrends(revenueTrends)
            .underutilizedBatches(underutilizedBatches)
            .overdueTasks(0) // Placeholder
            .upcomingBatches(upcomingBatches)
            .roleSpecificData(roleSpecificData)
            .generatedAt(LocalDate.now())
            .build();
    }
    
    /**
     * Generate custom report based on user role and parameters - Role-based access
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS') or hasRole('COUNSELLOR') or hasRole('PLACEMENT_OFFICER')")
    public Map<String, Object> generateCustomReport(String reportType, Map<String, Object> parameters) {
        Map<String, Object> report = new HashMap<>();
        
        switch (reportType.toLowerCase()) {
            case "enrollment_summary":
                report = generateEnrollmentSummary(parameters);
                break;
            case "revenue_analysis":
                report = generateRevenueAnalysis(parameters);
                break;
            case "placement_summary":
                report = generatePlacementSummary(parameters);
                break;
            case "lead_analysis":
                report = generateLeadAnalysis(parameters);
                break;
            default:
                report.put("error", "Unknown report type: " + reportType);
        }
        
        report.put("reportType", reportType);
        report.put("parameters", parameters);
        report.put("generatedAt", LocalDate.now());
        
        return report;
    }
    
    private Map<String, Object> generateEnrollmentSummary(Map<String, Object> parameters) {
        Map<String, Object> summary = new HashMap<>();
        
        // Get course-wise enrollment summary
        List<Object[]> courseEnrollments = studentRepository.countByCourse();
        Map<String, Integer> enrollmentsByCourse = courseEnrollments.stream()
            .collect(Collectors.toMap(
                data -> ((Course) data[0]).getName(),
                data -> ((Long) data[1]).intValue()
            ));
        
        summary.put("enrollmentsByCourse", enrollmentsByCourse);
        summary.put("totalEnrollments", enrollmentsByCourse.values().stream().mapToInt(Integer::intValue).sum());
        
        return summary;
    }
    
    private Map<String, Object> generateRevenueAnalysis(Map<String, Object> parameters) {
        Map<String, Object> analysis = new HashMap<>();
        
        // Get revenue by course
        List<Object[]> courseRevenue = courseRepository.getCourseRevenueReport();
        Map<String, BigDecimal> revenueByCourse = courseRevenue.stream()
            .collect(Collectors.toMap(
                data -> ((Course) data[0]).getName(),
                data -> data[1] != null ? (BigDecimal) data[1] : BigDecimal.ZERO
            ));
        
        BigDecimal totalRevenue = revenueByCourse.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        analysis.put("revenueByCourse", revenueByCourse);
        analysis.put("totalRevenue", totalRevenue);
        
        return analysis;
    }
    
    private Map<String, Object> generatePlacementSummary(Map<String, Object> parameters) {
        Map<String, Object> summary = new HashMap<>();
        
        // Get placement statistics
        List<Object[]> companyPlacements = placementRepository.countByCompany();
        Map<String, Integer> placementsByCompany = companyPlacements.stream()
            .collect(Collectors.toMap(
                data -> ((Company) data[0]).getName(),
                data -> ((Long) data[1]).intValue()
            ));
        
        long totalPlacements = placementRepository.countByStatus(Placement.PlacementStatus.PLACED);
        BigDecimal averageSalary = placementRepository.getAverageSalary();
        
        summary.put("placementsByCompany", placementsByCompany);
        summary.put("totalPlacements", totalPlacements);
        summary.put("averageSalary", averageSalary);
        
        return summary;
    }
    
    private Map<String, Object> generateLeadAnalysis(Map<String, Object> parameters) {
        Map<String, Object> analysis = new HashMap<>();
        
        // Get lead statistics
        List<Object[]> sourceData = leadRepository.countBySource();
        Map<String, Integer> leadsBySource = sourceData.stream()
            .collect(Collectors.toMap(
                data -> (String) data[0],
                data -> ((Long) data[1]).intValue()
            ));
        
        long totalLeads = leadRepository.count();
        long convertedLeads = leadRepository.countByStatus(Lead.LeadStatus.CONVERTED);
        double conversionRate = totalLeads > 0 ? ((double) convertedLeads / totalLeads) * 100 : 0.0;
        
        analysis.put("leadsBySource", leadsBySource);
        analysis.put("totalLeads", totalLeads);
        analysis.put("convertedLeads", convertedLeads);
        analysis.put("conversionRate", conversionRate);
        
        return analysis;
    }
}