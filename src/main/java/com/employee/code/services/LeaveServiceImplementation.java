package com.employee.code.services;

import com.employee.code.model.Employee;
import com.employee.code.model.Leave;
import com.employee.code.model.Manager;
import com.employee.code.repository.EmployeeRepository;
import com.employee.code.repository.LeaveRepository;
import com.employee.code.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class LeaveServiceImplementation implements LeaveService{

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private LeaveRepository leaveRepository;
    @Autowired
    private ManagerRepository managerRepository;

    @Override
    public Leave applyLeaveByEmployee(Leave leave, Long empid) {
        Employee emp = employeeRepository.findById(empid).orElse(null);
        if(emp!=null){
            leave.setEmployee(emp);
            leave.setStatus("PENDING");
            return leaveRepository.save(leave);
        }
        return null ;
    }

    @Override
    public List<Leave> viewLeavesByEmployee(Long empid) {
        return leaveRepository.findByEmployeeId(empid);
    }

    @Override
    public List<Leave> viewAllPendingLeaves() {
        return leaveRepository.findByStatus("PENDING");
    }

    @Override
    public Leave applyLeaveByManager(Leave leave, Long managerid) {
        Manager manager = managerRepository.findById(managerid).orElse(null);
        if(manager!=null){
            leave.setManager(manager);
            leave.setStatus("PENDING");
            return leaveRepository.save(leave);
        }
        return null ;
    }

    @Override
    public List<Leave> viewLeavesByManager(Long managerid) {
        return leaveRepository.findByManagerId(managerid);
    }

    @Override
    public String updateLeaveStatus(int leaveid, String status) {
        Leave leave = leaveRepository.findById(leaveid).orElse(null);
        if(leave!=null){
            leave.setStatus(status.toUpperCase());
            return "Leave Status Updated to:"+ status;

        }
        return "Leave ID not found";

    }
}
