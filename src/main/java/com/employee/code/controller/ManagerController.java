package com.employee.code.controller;

import com.employee.code.model.Duty;
import com.employee.code.model.Employee;
import com.employee.code.dto.EmployeeDTO;
import com.employee.code.model.Leave;
import com.employee.code.model.Manager;
import com.employee.code.security.JWTUtilizer;
import com.employee.code.services.AdminService;
import com.employee.code.services.DutyService;
import com.employee.code.services.LeaveService;
import com.employee.code.services.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manager")
@CrossOrigin("*")

public class ManagerController {
    @Autowired
    private JWTUtilizer jwtService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private DutyService dutyService;
    @Autowired
    private LeaveService leaveService ;

    private boolean isAuthorized(String authHeader, String expectedrole){
        try{
            String token = authHeader.substring(7);
            String role = jwtService.validateToken(token).get("role");

            return  role.equals(expectedrole);

        } catch (Exception e) {
            return  false;
        }
    }
    @GetMapping("/viewallemployees")
    public ResponseEntity<List<EmployeeDTO>> viewAllEmployees(@RequestHeader ("Authorization") String authHeader){
        if(!isAuthorized(authHeader,"MANAGER")){
            System.out.println("[ManagerController] Not authorized as MANAGER");
            return ResponseEntity.status(403).body(null);
        }
        String token = authHeader.substring(7);
        Long managerId = null;
        try {
            Object idObj = jwtService.validateToken(token).get("id");
            if (idObj instanceof Integer) {
                managerId = ((Integer) idObj).longValue();
            } else if (idObj instanceof Long) {
                managerId = (Long) idObj;
            } else if (idObj instanceof String) {
                managerId = Long.parseLong((String) idObj);
            }
        } catch (Exception e) {
            System.out.println("[ManagerController] JWT error: " + e.getMessage());
            return ResponseEntity.status(400).body(null);
        }
        if (managerId == null) {
            System.out.println("[ManagerController] Manager ID is null!");
            return ResponseEntity.status(400).body(null);
        }
        List<EmployeeDTO> dtos = managerService.findEmployeesByManagerId(managerId).stream().map(emp -> {
            EmployeeDTO dto = new EmployeeDTO();
            dto.setId(emp.getId());
            dto.setName(emp.getName());
            dto.setUsername(emp.getUsername());
            dto.setEmail(emp.getEmail());
            dto.setRole(emp.getRole());
            dto.setDepartment(emp.getDepartment());
            dto.setDesignation(emp.getDesignation());
            return dto;
        }).collect(java.util.stream.Collectors.toList());
        System.out.println("[ManagerController] Manager ID: " + managerId + ", Employees found: " + dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/updateemployeeAccountStatus")
    public ResponseEntity<String> updateEmployeeAccountStatus(@RequestParam Long empid ,@RequestParam String status,@RequestHeader ("Authorization") String authHeader){
        if(!isAuthorized(authHeader,"MANAGER")){
            return ResponseEntity.status(403).body("Access Denied Manger Privileges required");

        }
        String message = managerService.updateEmployeeAccountStatus(empid,status.toUpperCase());
        return ResponseEntity.ok(message);


    }
    @GetMapping("/viewownleaves")
    public ResponseEntity<List<Leave>> viewAllOwnLeaves(@RequestParam Long managerid ,@RequestHeader ("Authorization") String authHeader) {
        if (!isAuthorized(authHeader, "MANAGER")) {
            return ResponseEntity.status(403).body(null);

        }
        List<Leave> le = leaveService.viewLeavesByManager(managerid);
        return ResponseEntity.ok(le);
    }
    @PutMapping("/updateleavestatus")
    public ResponseEntity<String> updateEmployeeLeaveStatus(@RequestParam int leaveid ,@RequestParam String status,@RequestHeader("Authorization") String authHeader){
        if(!isAuthorized(authHeader,"MANAGER")){
            return ResponseEntity.status(403).body("Access Denied Manger Privileges required");

        }
        String result = leaveService.updateLeaveStatus(leaveid,status);
        return ResponseEntity.ok(result);


    }
    @PostMapping("/assigndutytoemployee")
    public ResponseEntity<String> assignDutyToEmployee(@RequestBody Duty duty,@RequestParam Long empid, @RequestParam long managerid , @RequestParam String status, @RequestHeader("Authorization") String authHeader){
        if(!isAuthorized(authHeader,"MANAGER")){
            return ResponseEntity.status(403).body("Access Denied Manger Privileges required");

        }
        dutyService.assignDutyByManagerToEmployee(duty,empid,managerid);
        return ResponseEntity.ok("Duty Assigned Successfully");


    }
    @GetMapping("/viewassignedduties")
    public  ResponseEntity<List<Duty>> viewAssignedDuties(@RequestParam Long managerid,@RequestHeader("Authorization") String authHeader){
        if(!isAuthorized(authHeader,"MANAGER")){
            return ResponseEntity.status(403).body(null);

        }
        List<Duty>  le = dutyService.viewDutiesAssignedByManager(managerid);
        return  ResponseEntity.ok(le);

    }


        @PostMapping("/applyleave")
    public ResponseEntity<Leave> applyLeave(@RequestBody Leave leave, @RequestParam Long managerid,@RequestHeader ("Authorization") String authHeader){
        if(!isAuthorized(authHeader,"MANAGER")){
            return ResponseEntity.status(403).body(null);

        }

        Leave l = leaveService.applyLeaveByManager(leave,managerid);
        return ResponseEntity.ok(l);

    }

    @GetMapping("/viewprofile")
    public ResponseEntity<Manager> viewProfile(@RequestParam Long managerid, @RequestHeader("Authorization") String authHeader) {
        if (!isAuthorized(authHeader, "MANAGER")) {
            return ResponseEntity.status(403).body(null);
        }
        return ResponseEntity.ok(managerService.findManagerById(managerid));
    }

    @PutMapping("/updateprofile")
    public ResponseEntity<String> updateProfile(@RequestBody Manager manager, @RequestHeader("Authorization") String authHeader) {
        if (!isAuthorized(authHeader, "MANAGER")) {
            return ResponseEntity.status(403).body("Access Denied! Manager privileges required");
        }
        try {
            Manager existingManager = managerService.findManagerById(manager.getId());
            if (existingManager == null) {
                return ResponseEntity.status(404).body("Manager not found");
            }
            
            // Update only allowed fields
            existingManager.setEmail(manager.getEmail());
            existingManager.setDepartment(manager.getDepartment());
            if (manager.getPassword() != null && !manager.getPassword().isEmpty()) {
                existingManager.setPassword(manager.getPassword());
            }
            existingManager.setName(manager.getName());
            existingManager.setUsername(manager.getUsername());
            existingManager.setContact(manager.getContact());
            
            managerService.updateManager(existingManager);
            return ResponseEntity.ok("Profile updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update profile: " + e.getMessage());
        }
    }

    @GetMapping("/recentactivities")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivities(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorized(authHeader, "MANAGER")) {
            return ResponseEntity.status(403).body(null);
        }
        // Placeholder: return a static list of activities
        List<Map<String, Object>> activities = new ArrayList<>();
        Map<String, Object> activity1 = new HashMap<>();
        activity1.put("type", "DUTY_ASSIGNED");
        activity1.put("description", "Duty 'Prepare Report' assigned to Employee 101");
        activity1.put("timestamp", System.currentTimeMillis());
        activities.add(activity1);
        Map<String, Object> activity2 = new HashMap<>();
        activity2.put("type", "LEAVE_APPLIED");
        activity2.put("description", "Employee 102 applied for leave");
        activity2.put("timestamp", System.currentTimeMillis() - 86400000);
        activities.add(activity2);
        return ResponseEntity.ok(activities);
    }

}
