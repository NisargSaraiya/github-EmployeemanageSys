package com.employee.code.repository;

import com.employee.code.model.Employee;
import com.employee.code.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    public List<Employee> findByNameContainingIgnoreCase(String name);
    public Employee findByUsernameOrEmailAndPassword(String username, String email, String password);
    public Employee findByUsernameAndPassword(String username, String password);
    public Employee findByUsername(String username);
    public Employee findByemail(String email);

    public Optional<Manager> findByEmail(String email);
}
