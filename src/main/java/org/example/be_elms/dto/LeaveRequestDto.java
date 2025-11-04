package org.example.be_elms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.be_elms.model.enums.RequestStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDto {
    private Integer id;
    private Integer employeeId;
    private String employeeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private RequestStatus status;
    private String rejectionReason;
    private Integer approvedById;
    private String approvedByName;
    private OffsetDateTime decidedAt;
    private Integer totalDays;
    private OffsetDateTime createdAt;
}

