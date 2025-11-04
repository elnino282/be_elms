package org.example.be_elms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceDto {
    private Integer id;
    private Integer employeeId;
    private String employeeName;
    private Integer year;
    private Integer entitlement;
    private Integer carriedOver;
    private Integer used;
    private Integer remaining;
}

