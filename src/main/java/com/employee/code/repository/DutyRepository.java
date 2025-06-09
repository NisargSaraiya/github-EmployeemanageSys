package com.employee.code.repository;

import com.employee.code.model.Duty;
import com.employee.code.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface DutyRepository extends JpaRepository<Duty,Integer> {
    public List<Duty> findByEmployee(Employee employee);
    public List<Duty> findByEmployeeId(Long id);
    @Query("SELECT d FROM Duty d WHERE d.assignedByManager.id = :managerId")
    public List<Duty> findByAssignedByManagerId(@Param("managerId") Long managerId);

    public List<Duty> findByAssignedByAdminId(Long adminid);

}
