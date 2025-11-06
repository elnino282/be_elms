package org.example.be_elms.service;

import lombok.RequiredArgsConstructor;
import org.example.be_elms.dto.CreateLeaveRequestDto;
import org.example.be_elms.dto.LeaveRequestDto;
import org.example.be_elms.exception.BadRequestException;
import org.example.be_elms.exception.ResourceNotFoundException;
import org.example.be_elms.exception.UnauthorizedException;
import org.example.be_elms.model.entity.Employee;
import org.example.be_elms.model.entity.LeaveBalance;
import org.example.be_elms.model.entity.LeaveRequest;
import org.example.be_elms.model.enums.RequestStatus;
import org.example.be_elms.model.enums.UserRole;
import org.example.be_elms.repository.EmployeeRepository;
import org.example.be_elms.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {
    
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceService leaveBalanceService;
    
    @Transactional
    public LeaveRequestDto createLeaveRequest(String employeeIdCode, CreateLeaveRequestDto dto) {
        Employee employee = employeeRepository.findByEmployeeIdCode(employeeIdCode)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        Integer employeeId = employee.getId();
        
        // Validate dates
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BadRequestException("End date must be after or equal to start date");
        }
        
        // Check for overlapping approved requests
        List<LeaveRequest> overlapping = leaveRequestRepository.findOverlappingApprovedRequests(
                employeeId, dto.getStartDate(), dto.getEndDate());
        
        if (!overlapping.isEmpty()) {
            throw new BadRequestException("You already have approved leave during this period");
        }
        
        // Calculate total days
        int totalDays = (int) java.time.temporal.ChronoUnit.DAYS.between(
                dto.getStartDate(), dto.getEndDate()) + 1;
        
        // Get or create balance for the year
        int year = dto.getStartDate().getYear();
        LeaveBalance balance = leaveBalanceService.getOrCreateBalance(employeeId, year);
        
        // Check if sufficient balance exists BEFORE creating request
        int remaining = balance.getRemaining();
        if (remaining < totalDays) {
            throw new BadRequestException(
                    String.format("Insufficient leave balance. You have %d days remaining but requested %d days", 
                            remaining, totalDays));
        }
        
        // Create leave request
        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setReason(dto.getReason());
        request.setStatus(RequestStatus.PENDING);
        
        LeaveRequest savedRequest = leaveRequestRepository.save(request);
        return convertToDto(savedRequest);
    }
    
    @Transactional(readOnly = true)
    public List<LeaveRequestDto> getEmployeeRequests(String employeeIdCode) {
        Employee employee = employeeRepository.findByEmployeeIdCode(employeeIdCode)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
        List<LeaveRequest> requests = leaveRequestRepository.findByEmployeeOrderByCreatedAtDesc(employee);
        return requests.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<LeaveRequestDto> getAllRequests() {
        List<LeaveRequest> requests = leaveRequestRepository.findAllByOrderByCreatedAtDesc();
        return requests.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    @Transactional
    public LeaveRequestDto approveRequest(Integer requestId, Integer adminId) {
        Employee admin = employeeRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        
        if (admin.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Only admins can approve requests");
        }
        
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
        
        // Can only approve PENDING requests
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Can only approve pending requests");
        }
        
        // Check balance again before approval
        int year = request.getStartDate().getYear();
        LeaveBalance balance = leaveBalanceService.getOrCreateBalance(
                request.getEmployee().getId(), year);
        
        int totalDays = request.getTotalDays();
        if (balance.getRemaining() < totalDays) {
            throw new BadRequestException("Employee has insufficient leave balance");
        }
        
        // Approve the request
        request.setStatus(RequestStatus.APPROVED);
        request.setApprovedBy(admin);
        request.setDecidedAt(OffsetDateTime.now());
        
        // Update leave balance
        leaveBalanceService.updateUsedDays(request.getEmployee().getId(), year, totalDays);
        
        LeaveRequest updatedRequest = leaveRequestRepository.save(request);
        return convertToDto(updatedRequest);
    }
    
    @Transactional
    public LeaveRequestDto rejectRequest(Integer requestId, Integer adminId, String rejectionReason) {
        Employee admin = employeeRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        
        if (admin.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Only admins can reject requests");
        }
        
        if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
            throw new BadRequestException("Rejection reason is required");
        }
        
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
        
        // Can only reject PENDING requests
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Can only reject pending requests");
        }
        
        // Reject the request
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionReason);
        request.setApprovedBy(admin);
        request.setDecidedAt(OffsetDateTime.now());
        
        // Do NOT update leave balance for rejected requests
        
        LeaveRequest updatedRequest = leaveRequestRepository.save(request);
        return convertToDto(updatedRequest);
    }
    
    private LeaveRequestDto convertToDto(LeaveRequest request) {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setId(request.getId());
        dto.setEmployeeId(request.getEmployee().getId());
        dto.setEmployeeName(request.getEmployee().getFullName());
        dto.setStartDate(request.getStartDate());
        dto.setEndDate(request.getEndDate());
        dto.setReason(request.getReason());
        dto.setStatus(request.getStatus());
        dto.setRejectionReason(request.getRejectionReason());
        dto.setTotalDays(request.getTotalDays());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setDecidedAt(request.getDecidedAt());
        
        // Set position and department from Employee
        dto.setPosition(request.getEmployee().getPosition());
        dto.setDepartment(request.getEmployee().getDepartment());
        
        // Format dateOfRequest from createdAt: "MM/dd/yyyy - HH:mm"
        if (request.getCreatedAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm");
            LocalDateTime localDateTime = request.getCreatedAt().toLocalDateTime();
            dto.setDateOfRequest(localDateTime.format(formatter));
        }
        
        // Format totalDaysTaken: "XX/YY days"
        Integer totalDays = request.getTotalDays();
        Integer annualLeaveEntitlement = request.getEmployee().getAnnualLeaveEntitlement();
        if (totalDays != null && annualLeaveEntitlement != null) {
            dto.setTotalDaysTaken(String.format("%02d/%d days", totalDays, annualLeaveEntitlement));
        }
        
        if (request.getApprovedBy() != null) {
            dto.setApprovedById(request.getApprovedBy().getId());
            dto.setApprovedByName(request.getApprovedBy().getFullName());
        }
        
        return dto;
    }
}

