package com.employee.code.services;

import com.employee.code.model.Duty;

import java.util.List;

public interface DutyService {
    public Duty assignDutyByAdminToEmployee(Duty duty , Long empid , int adminid);
    public Duty assignDutyByAdminToManager(Duty duty, Long managerid,int adminid);
    public Duty assignDutyByManagerToEmployee(Duty duty , Long empid , Long managerid);
    public List<Duty> viewAllDutiesofEmployee(Long eid);
    public List<Duty> viewDutiesAssignedByManager(Long managerid);
    public List<Duty> viewDutiesAssignedByAdmin(Long adminid);


}
