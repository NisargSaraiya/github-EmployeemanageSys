package com.employee.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.employee.code.model.Admin;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Integer> {
    public Admin findByUsernameAndPassword(String username,String password);

}
