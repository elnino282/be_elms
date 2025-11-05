package org.example.be_elms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.be_elms.dto.ApiResponse;
import org.example.be_elms.dto.ApproveRejectDto;
import org.example.be_elms.dto.CreateLeaveRequestDto;
import org.example.be_elms.dto.LeaveRequestDto;
import org.example.be_elms.service.LeaveRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LeaveRequestController {
    
    private final LeaveRequestService leaveRequestService;
    
    /**
     * Create a new leave request
     * EMPLOYEE role - creates request for themselves
     * 
     * @param employeeId - Employee ID passed as request parameter or from auth context
     * @param dto - Leave request details
     */
    @PostMapping
    public ResponseEntity<ApiResponse<LeaveRequestDto>> createLeaveRequest(
            @RequestParam Integer employeeId,
            @Valid @RequestBody CreateLeaveRequestDto dto) {
        
        LeaveRequestDto result = leaveRequestService.createLeaveRequest(employeeId, dto);
        return ResponseEntity.ok(ApiResponse.success("Leave request created successfully", result));
    }
    
    /**
     * Get leave requests for a specific employee
     * EMPLOYEE role - can only view their own requests
     */
    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse<List<LeaveRequestDto>>> getMyRequests(
            @RequestParam Integer employeeId) {
        
        List<LeaveRequestDto> requests = leaveRequestService.getEmployeeRequests(employeeId);
        return ResponseEntity.ok(ApiResponse.success(requests));
    }
    
    /**
     * Get all leave requests
     * ADMIN role only
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<LeaveRequestDto>>> getAllRequests() {
        List<LeaveRequestDto> requests = leaveRequestService.getAllRequests();
        return ResponseEntity.ok(ApiResponse.success(requests));
    }
    
    /**
     * Approve a leave request
     * ADMIN role only
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<LeaveRequestDto>> approveRequest(
            @PathVariable Integer id,
            @RequestParam Integer adminId) {
        
        LeaveRequestDto result = leaveRequestService.approveRequest(id, adminId);
        return ResponseEntity.ok(ApiResponse.success("Leave request approved successfully", result));
    }
    
    /**
     * Reject a leave request
     * ADMIN role only
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<LeaveRequestDto>> rejectRequest(
            @PathVariable Integer id,
            @RequestParam Integer adminId,
            @Valid @RequestBody ApproveRejectDto dto) {
        
        LeaveRequestDto result = leaveRequestService.rejectRequest(id, adminId, dto.getRejectionReason());
        return ResponseEntity.ok(ApiResponse.success("Leave request rejected", result));
    }
}

