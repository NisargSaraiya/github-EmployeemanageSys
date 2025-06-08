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
    public Employee findByUsernameandPassword(String username, String password);
    public Employee findByUsername(String username);
    public Employee findByEmail(String email);

    public Optional<Manager> FindByEmail(String email);
}
