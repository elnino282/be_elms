package org.example.be_elms.controller;

import lombok.RequiredArgsConstructor;
import org.example.be_elms.dto.ApiResponse;
import org.example.be_elms.dto.LeaveBalanceDto;
import org.example.be_elms.service.LeaveBalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leave-balances")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LeaveBalanceController {
    
    private final LeaveBalanceService leaveBalanceService;
    
    /**
     * Get leave balance for current employee
     * EMPLOYEE role - can only view their own balance
     */
    @GetMapping("/my-balance")
    public ResponseEntity<ApiResponse<LeaveBalanceDto>> getMyBalance(
            @RequestParam Integer employeeId,
            @RequestParam(required = false) Integer year) {
        
        LeaveBalanceDto balance;
        if (year != null) {
            balance = leaveBalanceService.getEmployeeBalance(employeeId, year);
        } else {
            balance = leaveBalanceService.getCurrentYearBalance(employeeId);
        }
        
        return ResponseEntity.ok(ApiResponse.success(balance));
    }
    
    /**
     * Get all employees' leave balances
     * ADMIN role only
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<LeaveBalanceDto>>> getAllBalances(
            @RequestParam(required = false) Integer year) {
        
        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        List<LeaveBalanceDto> balances = leaveBalanceService.getAllBalances(targetYear);
        
        return ResponseEntity.ok(ApiResponse.success(balances));
    }
}

