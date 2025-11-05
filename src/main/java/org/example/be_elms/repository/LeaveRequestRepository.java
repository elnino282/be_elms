package org.example.be_elms.repository;

import org.example.be_elms.model.entity.Employee;
import org.example.be_elms.model.entity.LeaveRequest;
import org.example.be_elms.model.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {
    List<LeaveRequest> findByEmployeeOrderByCreatedAtDesc(Employee employee);
    List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(Integer employeeId);
    List<LeaveRequest> findByStatusOrderByCreatedAtDesc(RequestStatus status);
    List<LeaveRequest> findAllByOrderByCreatedAtDesc();
    
    // Check for overlapping approved leave requests
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.id = :employeeId " +
           "AND lr.status = 'APPROVED' " +
           "AND ((lr.startDate <= :endDate AND lr.endDate >= :startDate))")
    List<LeaveRequest> findOverlappingApprovedRequests(
        @Param("employeeId") Integer employeeId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}

