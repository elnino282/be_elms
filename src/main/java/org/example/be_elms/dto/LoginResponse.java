package org.example.be_elms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.be_elms.model.enums.UserRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Integer id;
    private String employeeIdCode;
    private String username;
    private String fullName;
    private UserRole role;
    private String position;
    private String department;
    private String message;
}

