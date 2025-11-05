package org.example.be_elms.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.be_elms.model.enums.UserRole;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "employee_id_code", unique = true, length = 10)
    private String employeeIdCode;

    @Column(unique = true, nullable = false, length = 100)
    private String username;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Column(name = "full_name", length = 255)
    private String fullName;
    
    @Column(nullable = false, columnDefinition = "user_role")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private UserRole role = UserRole.EMPLOYEE;
    
    @Column(name = "annual_leave_entitlement", nullable = false)
    private Integer annualLeaveEntitlement = 12;
    
    @Column(name = "accumulated_leave_days", nullable = false)
    private Integer accumulatedLeaveDays = 0;
    
    @Column(length = 255)
    private String position;

    @Column(length = 255)
    private String department;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}

