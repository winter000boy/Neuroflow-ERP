package com.institute.management.service;

import com.institute.management.entity.Company;
import com.institute.management.entity.Placement;
import com.institute.management.entity.Student;
import com.institute.management.repository.PlacementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PlacementService {
    
    @Autowired
    private PlacementRepository placementRepository;
    
    /**
     * Create a new placement record - Only ADMIN and PLACEMENT_OFFICER can create placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Placement createPlacement(Placement placement) {
        return placementRepository.save(placement);
    }
    
    /**
     * Update an existing placement - Only ADMIN and PLACEMENT_OFFICER can update placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Placement updatePlacement(UUID id, Placement placementDetails) {
        Placement placement = placementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Placement not found with id: " + id));
        
        placement.setStudent(placementDetails.getStudent());
        placement.setCompany(placementDetails.getCompany());
        placement.setPosition(placementDetails.getPosition());
        placement.setSalary(placementDetails.getSalary());
        placement.setPlacementDate(placementDetails.getPlacementDate());
        placement.setStatus(placementDetails.getStatus());
        
        return placementRepository.save(placement);
    }
    
    /**
     * Get placement by ID - ADMIN and PLACEMENT_OFFICER can view placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Optional<Placement> getPlacementById(UUID id) {
        return placementRepository.findById(id);
    }
    
    /**
     * Get all placements with pagination - ADMIN and PLACEMENT_OFFICER can view placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Page<Placement> getAllPlacements(Pageable pageable) {
        return placementRepository.findAll(pageable);
    }
    
    /**
     * Get placements by student - ADMIN and PLACEMENT_OFFICER can view placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Placement> getPlacementsByStudent(Student student) {
        return placementRepository.findByStudent(student);
    }
    
    /**
     * Get placements by company - ADMIN and PLACEMENT_OFFICER can view placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Placement> getPlacementsByCompany(Company company) {
        return placementRepository.findByCompany(company);
    }
    
    /**
     * Get placements by status - ADMIN and PLACEMENT_OFFICER can view placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Placement> getPlacementsByStatus(Placement.PlacementStatus status) {
        return placementRepository.findByStatus(status);
    }
    
    /**
     * Get placements within date range - ADMIN and PLACEMENT_OFFICER can view placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Placement> getPlacementsByDateRange(LocalDate startDate, LocalDate endDate) {
        return placementRepository.findByPlacementDateBetween(startDate, endDate);
    }
    
    /**
     * Get placements within salary range - ADMIN and PLACEMENT_OFFICER can view placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Placement> getPlacementsBySalaryRange(BigDecimal minSalary, BigDecimal maxSalary) {
        return placementRepository.findBySalaryBetween(minSalary, maxSalary);
    }
    
    /**
     * Update placement status - Only ADMIN and PLACEMENT_OFFICER can update status
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Placement updatePlacementStatus(UUID placementId, Placement.PlacementStatus status) {
        Placement placement = placementRepository.findById(placementId)
            .orElseThrow(() -> new RuntimeException("Placement not found with id: " + placementId));
        
        placement.setStatus(status);
        return placementRepository.save(placement);
    }
    
    /**
     * Delete placement - Only ADMIN can delete placements
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePlacement(UUID id) {
        if (!placementRepository.existsById(id)) {
            throw new RuntimeException("Placement not found with id: " + id);
        }
        placementRepository.deleteById(id);
    }
    
    /**
     * Get placement statistics - ADMIN and PLACEMENT_OFFICER can view statistics
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public PlacementStatistics getPlacementStatistics() {
        long totalPlacements = placementRepository.count();
        long activePlacements = placementRepository.countByStatus(Placement.PlacementStatus.PLACED);
        
        // Calculate average salary
        BigDecimal averageSalary = placementRepository.getAverageSalary();
        if (averageSalary == null) {
            averageSalary = BigDecimal.ZERO;
        }
        
        // Calculate placement rate (this would need student count from StudentRepository)
        // For now, returning basic statistics
        
        return new PlacementStatistics(totalPlacements, activePlacements, averageSalary);
    }
    
    /**
     * Get placement count by company - ADMIN and PLACEMENT_OFFICER can view statistics
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public List<Object[]> getPlacementCountByCompany() {
        return placementRepository.countByCompany();
    }
    
    /**
     * Get recent placements - ADMIN and PLACEMENT_OFFICER can view recent placements
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('PLACEMENT_OFFICER')")
    public Page<Placement> getRecentPlacements(Pageable pageable) {
        return placementRepository.findRecentPlacements(pageable);
    }
    
    /**
     * Inner class for placement statistics
     */
    public static class PlacementStatistics {
        private final long totalPlacements;
        private final long activePlacements;
        private final BigDecimal averageSalary;
        
        public PlacementStatistics(long totalPlacements, long activePlacements, BigDecimal averageSalary) {
            this.totalPlacements = totalPlacements;
            this.activePlacements = activePlacements;
            this.averageSalary = averageSalary;
        }
        
        public long getTotalPlacements() { return totalPlacements; }
        public long getActivePlacements() { return activePlacements; }
        public BigDecimal getAverageSalary() { return averageSalary; }
        public double getPlacementRate() {
            return totalPlacements > 0 ? (double) activePlacements / totalPlacements * 100 : 0.0;
        }
    }
}