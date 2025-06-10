package com.employee.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.employee.code.model.Manager;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
    @Query("SELECT m FROM Manager m WHERE (m.username = :identifier OR m.email = :identifier) AND m.password = :password")
    Manager findByUsernameOrEmailAndPassword(@Param("identifier") String identifier, @Param("password") String password);

    @Query("SELECT m FROM Manager m WHERE m.username = :username AND m.password = :password")
    Manager findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    @Query("SELECT m FROM Manager m WHERE m.email = :email AND m.password = :password")
    Manager findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    @Query("SELECT m FROM Manager m WHERE m.username = :username")
    Optional<Manager> findByUsername(@Param("username") String username);

    @Query("SELECT m FROM Manager m WHERE m.email = :email")
    Optional<Manager> findByEmail(@Param("email") String email);

    @Query("SELECT m FROM Manager m WHERE m.id = :id")
    Optional<Manager> findById(@Param("id") Long id);
}
