package com.employee.code.services;

import com.employee.code.model.Admin;
import com.employee.code.model.Duty;
import com.employee.code.model.Employee;
import com.employee.code.model.Manager;
import com.employee.code.repository.AdminRepository;
import com.employee.code.repository.DutyRepository;
import com.employee.code.repository.EmployeeRepository;
import com.employee.code.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DutyServiceImplementation implements DutyService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private DutyRepository dutyRepository;
    @Autowired
    private ManagerRepository managerRepository;
    @Override
    public Duty assignDutyByAdminToEmployee(Duty duty, Long empid, int adminid) {
        Employee emp = employeeRepository.findById(empid).orElse(null);
        Admin admin = adminRepository.findById(adminid).orElse(null);
        if(emp!= null && admin !=null){
            duty.setEmployee(emp);
            duty.setAssignedByAdmin(admin);
            return dutyRepository.save(duty);
        }
        return null;
    }

    @Override
    public Duty assignDutyByAdminToManager(Duty duty, Long managerid, int adminid) {
        Manager manager = managerRepository.findById(managerid).orElse(null);
        Admin admin = adminRepository.findById(adminid).orElse(null);
        if(manager!= null && admin !=null){
            duty.setEmployee(null);
            duty.setManager(manager);
            duty.setAssignedByAdmin(admin);
            return dutyRepository.save(duty);
        }
        return null;
    }

    @Override
    public Duty assignDutyByManagerToEmployee(Duty duty, Long empid, Long managerid) {
        Employee emp = employeeRepository.findById(empid).orElse(null);
        Manager manager = managerRepository.findById(managerid).orElse(null);
        if(emp!= null && manager !=null){
            duty.setEmployee(emp);
            duty.setAssignedByManager(manager);
            return dutyRepository.save(duty);
        }
        return null;
    }

    @Override
    public List<Duty> viewAllDutiesOfEmployee(Long eid) {
        return dutyRepository.findByEmployeeId(eid);
    }


    @Override
    public List<Duty> viewDutiesAssignedByManager(Long managerid) {
        return dutyRepository.findByAssignedByManagerId(managerid);
    }

    @Override
    public List<Duty> viewDutiesAssignedByAdmin(Long aid) {
        return dutyRepository.findByAssignedByAdminId(aid);
    }
}

