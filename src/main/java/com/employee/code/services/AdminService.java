package com.employee.code.services;

import com.employee.code.model.*;

import java.util.List;

public interface AdminService {
    public Admin checkadminlogin(String username,String password);

    public Manager addManager(Manager manager);
    public List<Manager> viewAllManagers();
    public String deleteManager(long mid);
    public List<Employee> viewAllEmployees();
    public String deleteEmployee(long eid);
    public long managercount();
    public long employeecount();
    public List<Leave> viewAllLeaveApplications();

}
