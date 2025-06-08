package com.employee.code.services;

import com.employee.code.model.Duty;
import com.employee.code.model.Employee;

import java.util.List;

public interface EmployeeService {
    public Employee checkemployeelogin(String username , String password);
    public String registerEmployee(Employee emp);
    public String updateEmployeeProfile(Employee emp);
    public Employee findEmployeeById(long id);
    public Employee findEmployeeByUsername(String username);
    public Employee findEmployeeByEmail(String email);
    public List<Employee> viewAllEmployee();
    public String updateAccountStatus(Long empid,String status );
    public List<Duty> viewAssignedDuties(Long empid);

    public String generateResetToken(String email);
    public boolean validateResetToken(String token);
    public boolean changePassword(Employee employee, String oldPassword , String newPassword);
    public  void updatePassword(String token,String newPassword);
    public void deleteResetToken(String token);
    public boolean isTokenExpired(String token);
}

