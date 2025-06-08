package com.employee.code.services;

import com.employee.code.model.Leave;

import java.util.List;

public interface LeaveService {
    public Leave applyLeaveByEmployee(Leave leave, Long empid);
    public List<Leave> viewLeavesByEmployee(Long empid);
    public List<Leave> viewAllPendingLeaves();
    public Leave applyLeaveByManager(Leave leave,Long managerid);
    public List<Leave> viewLeavesByManager(Long managerid);
    public String updateLeaveStatus(int leaveid,String status);

}
