package com.employee.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.employee.code.model.Manager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager,Long> {
    public Manager findByUsernameandPassword(String username,String password);
    public Manager findByUsername(String username);
    public Manager findByemail(String email);
    public Optional<Manager> FindByEmail(String email);
}
