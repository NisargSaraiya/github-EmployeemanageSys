package com.employee.code.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;
@Entity
@Table(name = "employeetable")
public class Employee {
    @Id
    @Column(name = "employee_id")

    private Long id ;
    @Column(name = "employee_name", nullable=false)
    private String name ;
    @Column(name = "employee_gender", nullable=false)
    private String gender;
    @Column(name = "employee_age", nullable=false)
    private int age ;
    @Column(name = "employee_designation", nullable=false)
    private String designation ;
    @Column(name = "employee_department", nullable=false)
    private String department ;

    @Column(name = "employee_salary", nullable=false)
    private double salary ;
    @Column(name = "employee_email", nullable=false,unique = true)
    private String email ;
    @Column(name = "employee_password", nullable=false)
    private String password ;

    @Column(name = "employee_username", nullable=false,unique = true)
    private String username ;
    @Column(name = "employee_contact", nullable=false,unique = true)
    private String contact;
    @Column(name = "account_status",nullable = false)
    private String accountstatus; // e.g. Accepted, Pending, Rejected
    @Column(name = "employee_role",nullable = false)
    private String role;



    @OneToMany(mappedBy = "employee",cascade = CascadeType.ALL)
    private List<Leave> leave ;
@OneToMany(mappedBy = "employee",cascade = CascadeType.ALL)
@JsonManagedReference
private List<Duty> duty ;
@ManyToOne
@JoinColumn(name="manager_id")
@JsonBackReference
private Manager manager ;
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public List<Leave> getLeave() {
        return leave;
    }

    public void setLeave(List<Leave> leave) {
        this.leave = leave;
    }

    public List<Duty> getDuty() {
        return duty;
    }

    public void setDuty(List<Duty> duty) {
        this.duty = duty;
    }

    public String getAccountstatus() {
        return accountstatus;
    }

    public void setAccountstatus(String accountstatus) {
        this.accountstatus = accountstatus;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
