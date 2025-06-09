package com.employee.code.services;

import com.employee.code.model.Employee;
import com.employee.code.model.Manager;

import java.util.List;

public interface ManagerService {
    public Manager  checkmanagerlogin(String identifier,String password);
    public Manager findManagerById(long id);
    public Manager findManagerByUsername(String username);
    public Manager findManagerByEmail(String email);
    public List<Manager> viewAllManagers();
    public List<Employee> viewAllEmployees();
    public String updateEmployeeAccountStatus(Long employeeid,String status);
    public String generateResetToken(String email);
    public boolean validateResetToken(String token);
    public boolean changePassword(Manager manager,String oldPassword , String newPassword);
    public  void updatePassword(String token,String newPassword);
    public void deleteResetToken(String token);
    public boolean isTokenExpired(String token);
}
