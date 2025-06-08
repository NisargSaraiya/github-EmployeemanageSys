package com.employee.code.repository;

import com.employee.code.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave,Integer> {
    public List<Leave> findByEmployeeId(Long eid);
    public List<Leave> findByManagerId(Long mid);
    public List<Leave> findByStatus(String status);
}
