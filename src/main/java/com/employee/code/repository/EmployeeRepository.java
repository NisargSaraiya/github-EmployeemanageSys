package com.employee.code.repository;

import com.employee.code.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByManagerId(Long managerId);
    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Employee> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT e FROM Employee e WHERE (e.username = :username OR e.email = :email) AND e.password = :password")
    Optional<Employee> findByUsernameOrEmailAndPassword(
        @Param("username") String username,
        @Param("email") String email,
        @Param("password") String password
    );

    @Query("SELECT e FROM Employee e WHERE e.username = :username AND e.password = :password")
    Optional<Employee> findByUsernameAndPassword(
        @Param("username") String username,
        @Param("password") String password
    );

    @Query("SELECT e FROM Employee e WHERE e.email = :email AND e.password = :password")
    Optional<Employee> findByEmailAndPassword(
        @Param("email") String email,
        @Param("password") String password
    );

    @Query("SELECT e FROM Employee e WHERE e.username = :username")
    Optional<Employee> findByUsername(@Param("username") String username);

    @Query("SELECT e FROM Employee e WHERE e.email = :email")
    Optional<Employee> findByEmail(@Param("email") String email);

    @Query("SELECT e FROM Employee e WHERE e.id = :id")
    Optional<Employee> findById(@Param("id") Long id);
}
