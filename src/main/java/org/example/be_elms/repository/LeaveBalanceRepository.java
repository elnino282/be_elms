package org.example.be_elms.repository;

import org.example.be_elms.model.entity.Employee;
import org.example.be_elms.model.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Integer> {
    Optional<LeaveBalance> findByEmployeeAndYear(Employee employee, Integer year);
    Optional<LeaveBalance> findByEmployeeIdAndYear(Integer employeeId, Integer year);
}

