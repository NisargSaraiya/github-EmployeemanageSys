package com.employee.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.employee.code.model.Admin;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    @Query("SELECT a FROM Admin a WHERE a.username = :username AND a.password = :password")
    Admin findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    @Query("SELECT a FROM Admin a WHERE a.email = :email AND a.password = :password")
    Admin findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    @Query("SELECT a FROM Admin a WHERE (a.username = :identifier OR a.email = :identifier) AND a.password = :password")
    Admin findByUsernameOrEmailAndPassword(@Param("identifier") String identifier, @Param("password") String password);
}
