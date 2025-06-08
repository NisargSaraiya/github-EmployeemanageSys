package com.employee.code.repository;

import com.employee.code.model.Duty;
import com.employee.code.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface DutyRepository extends JpaRepository<Duty,Integer> {
    public List<Duty> findByEmployee(Employee employee);
    public List<Duty> findByEmployeeId(Long id);
    public List<Duty> findByAssignedByManager(Long managerid);
    public List<Duty> findByAssignedByAdmin(Long adminid);

}
