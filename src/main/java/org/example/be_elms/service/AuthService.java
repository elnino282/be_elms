package org.example.be_elms.service;

import lombok.RequiredArgsConstructor;
import org.example.be_elms.dto.LoginRequest;
import org.example.be_elms.dto.LoginResponse;
import org.example.be_elms.dto.UserInfoDto;
import org.example.be_elms.exception.ResourceNotFoundException;
import org.example.be_elms.exception.UnauthorizedException;
import org.example.be_elms.model.entity.Employee;
import org.example.be_elms.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Employee employee = employeeRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        // Plain text password comparison (as per schema requirement)
        if (!employee.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        return new LoginResponse(
                employee.getId(),
                employee.getEmployeeIdCode(),
                employee.getUsername(),
                employee.getFullName(),
                employee.getRole(),
                employee.getPosition(),
                employee.getDepartment(),
                "Login successful"
        );
    }

    @Transactional(readOnly = true)
    public UserInfoDto getUserInfo(Integer employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        return new UserInfoDto(
                employee.getEmployeeIdCode(),
                employee.getUsername(),
                employee.getFullName(),
                employee.getRole(),
                employee.getPosition(),
                employee.getDepartment()
        );
    }
}

