package com.institute.management.controller;

import com.institute.management.dto.*;
import com.institute.management.entity.Lead;
import com.institute.management.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leads")
@Tag(name = "Lead Management", description = "APIs for managing leads and lead conversion")
public class LeadController {
    
    @Autowired
    private LeadService leadService;
    
    @Operation(summary = "Create a new lead", description = "Create a new lead with contact information and course interest")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Lead created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Lead with email or phone already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    public ResponseEntity<LeadResponseDTO> createLead(@Valid @RequestBody LeadCreateRequestDTO createRequest) {
        LeadResponseDTO createdLead = leadService.createLead(createRequest);
        return new ResponseEntity<>(createdLead, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Get all leads", description = "Get all leads with pagination, sorting, and filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Leads retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public ResponseEntity<Page<LeadResponseDTO>> getAllLeads(
            @Parameter(description = "Filter by lead status") @RequestParam(required = false) Lead.LeadStatus status,
            @Parameter(description = "Filter by lead source") @RequestParam(required = false) String source,
            @Parameter(description = "Filter by course interest") @RequestParam(required = false) String courseInterest,
            @Parameter(description = "Filter by assigned counsellor ID") @RequestParam(required = false) UUID counsellorId,
            @Parameter(description = "Search term for name, email, or phone") @RequestParam(required = false) String searchTerm,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<LeadResponseDTO> leads = leadService.getAllLeads(status, source, courseInterest, counsellorId, searchTerm, pageable);
        return ResponseEntity.ok(leads);
    }
    
    @Operation(summary = "Get lead by ID", description = "Get a specific lead by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lead retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LeadResponseDTO> getLeadById(@PathVariable UUID id) {
        LeadResponseDTO lead = leadService.getLeadById(id);
        return ResponseEntity.ok(lead);
    }
    
    @Operation(summary = "Update a lead", description = "Update an existing lead's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lead updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "409", description = "Lead with email or phone already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}")
    public ResponseEntity<LeadResponseDTO> updateLead(@PathVariable UUID id, 
                                                     @Valid @RequestBody LeadUpdateRequestDTO updateRequest) {
        LeadResponseDTO updatedLead = leadService.updateLead(id, updateRequest);
        return ResponseEntity.ok(updatedLead);
    }
    
    @Operation(summary = "Delete a lead", description = "Delete a lead by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Lead deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "400", description = "Cannot delete converted lead"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable UUID id) {
        leadService.deleteLead(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Get leads by status", description = "Get leads filtered by status with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Leads retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<LeadResponseDTO>> getLeadsByStatus(
            @PathVariable Lead.LeadStatus status,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<LeadResponseDTO> leads = leadService.getLeadsByStatus(status, pageable);
        return ResponseEntity.ok(leads);
    }
    
    @Operation(summary = "Get leads by counsellor", description = "Get leads assigned to a specific counsellor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Leads retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Counsellor not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/counsellor/{counsellorId}")
    public ResponseEntity<Page<LeadResponseDTO>> getLeadsByCounsellor(
            @PathVariable UUID counsellorId,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<LeadResponseDTO> leads = leadService.getLeadsByCounsellor(counsellorId, pageable);
        return ResponseEntity.ok(leads);
    }
    
    @Operation(summary = "Convert lead to student", description = "Convert a lead to a student record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lead converted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid conversion data or lead already converted"),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/{id}/convert")
    public ResponseEntity<StudentResponseDTO> convertLeadToStudent(@PathVariable UUID id, 
                                                                 @Valid @RequestBody LeadConversionRequestDTO conversionRequest) {
        StudentResponseDTO student = leadService.convertLeadToStudent(id, conversionRequest);
        return ResponseEntity.ok(student);
    }
    
    @Operation(summary = "Add follow-up to lead", description = "Add a follow-up note and schedule next action for a lead")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Follow-up added successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid follow-up data or lead already converted"),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/{id}/follow-up")
    public ResponseEntity<LeadResponseDTO> addFollowUp(@PathVariable UUID id, 
                                                      @Valid @RequestBody LeadFollowUpRequestDTO followUpRequest) {
        LeadResponseDTO updatedLead = leadService.addFollowUp(id, followUpRequest);
        return ResponseEntity.ok(updatedLead);
    }
    
    @Operation(summary = "Get leads requiring follow-up", description = "Get leads that require follow-up based on scheduled dates")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Leads retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/follow-up/required")
    public ResponseEntity<List<LeadResponseDTO>> getLeadsRequiringFollowUp() {
        List<LeadResponseDTO> leads = leadService.getLeadsRequiringFollowUp();
        return ResponseEntity.ok(leads);
    }
    
    @Operation(summary = "Get leads without follow-up", description = "Get leads that don't have any follow-up scheduled")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Leads retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/follow-up/missing")
    public ResponseEntity<List<LeadResponseDTO>> getLeadsWithoutFollowUp() {
        List<LeadResponseDTO> leads = leadService.getLeadsWithoutFollowUp();
        return ResponseEntity.ok(leads);
    }
    
    @Operation(summary = "Get lead statistics", description = "Get comprehensive statistics about leads")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/statistics")
    public ResponseEntity<LeadService.LeadStatsDTO> getLeadStatistics() {
        LeadService.LeadStatsDTO stats = leadService.getLeadStatistics();
        return ResponseEntity.ok(stats);
    }
}