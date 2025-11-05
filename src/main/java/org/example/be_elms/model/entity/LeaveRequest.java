package org.example.be_elms.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.be_elms.model.enums.RequestStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "leave_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column(nullable = false, columnDefinition = "request_status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private RequestStatus status = RequestStatus.PENDING;
    
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;
    
    @Column(name = "decided_at")
    private OffsetDateTime decidedAt;
    
    // Note: balance_year and total_days are generated columns in DB
    // We can calculate them in Java for convenience
    @Transient
    public Integer getBalanceYear() {
        return startDate != null ? startDate.getYear() : null;
    }
    
    @Transient
    public Integer getTotalDays() {
        if (startDate != null && endDate != null) {
            return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
        return null;
    }
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}

