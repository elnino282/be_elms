package org.example.be_elms.service;

import lombok.RequiredArgsConstructor;
import org.example.be_elms.dto.LeaveBalanceDto;
import org.example.be_elms.exception.ResourceNotFoundException;
import org.example.be_elms.model.entity.Employee;
import org.example.be_elms.model.entity.LeaveBalance;
import org.example.be_elms.repository.EmployeeRepository;
import org.example.be_elms.repository.LeaveBalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveBalanceService {
    
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    
    @Transactional(readOnly = true)
    public LeaveBalanceDto getEmployeeBalance(Integer employeeId, Integer year) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeAndYear(employee, year)
                .orElseGet(() -> {
                    // Auto-create balance for the year if it doesn't exist
                    LeaveBalance newBalance = new LeaveBalance();
                    newBalance.setEmployee(employee);
                    newBalance.setYear(year);
                    newBalance.setEntitlement(employee.getAnnualLeaveEntitlement());
                    newBalance.setCarriedOver(0);
                    newBalance.setUsed(0);
                    return leaveBalanceRepository.save(newBalance);
                });
        
        return convertToDto(balance);
    }
    
    @Transactional(readOnly = true)
    public LeaveBalanceDto getCurrentYearBalance(Integer employeeId) {
        int currentYear = LocalDate.now().getYear();
        return getEmployeeBalance(employeeId, currentYear);
    }
    
    @Transactional(readOnly = true)
    public List<LeaveBalanceDto> getAllBalances(Integer year) {
        List<LeaveBalance> balances = leaveBalanceRepository.findAll().stream()
                .filter(lb -> lb.getYear().equals(year))
                .collect(Collectors.toList());
        
        return balances.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void updateUsedDays(Integer employeeId, Integer year, int days) {
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, year)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found for year " + year));
        
        balance.setUsed(balance.getUsed() + days);
        leaveBalanceRepository.save(balance);
    }
    
    @Transactional(readOnly = true)
    public LeaveBalance getOrCreateBalance(Integer employeeId, Integer year) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
        return leaveBalanceRepository.findByEmployeeAndYear(employee, year)
                .orElseGet(() -> {
                    LeaveBalance newBalance = new LeaveBalance();
                    newBalance.setEmployee(employee);
                    newBalance.setYear(year);
                    newBalance.setEntitlement(employee.getAnnualLeaveEntitlement());
                    newBalance.setCarriedOver(0);
                    newBalance.setUsed(0);
                    return leaveBalanceRepository.save(newBalance);
                });
    }
    
    private LeaveBalanceDto convertToDto(LeaveBalance balance) {
        return new LeaveBalanceDto(
                balance.getId(),
                balance.getEmployee().getId(),
                balance.getEmployee().getFullName(),
                balance.getYear(),
                balance.getEntitlement(),
                balance.getCarriedOver(),
                balance.getUsed(),
                balance.getRemaining()
        );
    }
}

