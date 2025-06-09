package com.employee.code.controller;

import com.employee.code.model.*;
import com.employee.code.security.JWTUtilizer;
import com.employee.code.services.AdminService;
import com.employee.code.services.DutyService;
import com.employee.code.services.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class AdminController {
    @Autowired
    private JWTUtilizer jwtService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private DutyService dutyService;

    @PostMapping("/addmanager")
    public ResponseEntity<String> addManager(@RequestBody Manager manager, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token).get("role").equals("ADMIN")) {
            return ResponseEntity.status(403).body("Access Denied! Admin privilages required");

        }
        adminService.addManager(manager);
        return ResponseEntity.ok("Manager Added Succesfully\n" + "\nManagerId:" + manager.getId());


    }
    
    @GetMapping("viewallmanagers")
    public ResponseEntity<List<Manager>> viewAllManagers(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token).get("role").equals("ADMIN")) {
            return ResponseEntity.status(403).body(null);

        }
        return ResponseEntity.ok(adminService.viewAllManagers());
    }

    @GetMapping("viewallemployees")
    public ResponseEntity<List<Employee>> viewAllEmployees(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token).get("role").equals("ADMIN")) {
            return ResponseEntity.status(403).body(null);

        }
        return ResponseEntity.ok(adminService.viewAllEmployees());
    }

    @PutMapping("/assigndutytomanager")
    public ResponseEntity<String> assignDutyToManager(@RequestBody Duty duty, @RequestParam String assingeeRole, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token).get("role").equals("ADMIN")) {
            return ResponseEntity.status(403).body("Access Denied! Admin privilages required");

        }
        Manager m = new Manager();
        Admin a = new Admin();
        dutyService.assignDutyByAdminToManager(duty, m.getId(), a.getId());
        return ResponseEntity.ok("Duty assigned to manager Successfully");

    }

    @PutMapping("/updateemployeeAccountStatus")
    public ResponseEntity<String> updateEmployeeAccountStatus(@RequestParam Long empid, @RequestParam String status, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token).get("role").equals("ADMIN")) {
            return ResponseEntity.status(403).body("Access Denied! Admin privilages required");
        }
        String message = managerService.updateEmployeeAccountStatus(empid, status.toUpperCase());
        return ResponseEntity.ok(message);

    }

    @PutMapping("/assigndutytoemployee")
    public ResponseEntity<String> assignDutyToEmployee(@RequestBody Duty duty, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token).get("role").equals("ADMIN")) {
            return ResponseEntity.status(403).body("Access Denied! Admin privilages required");

        }
        Employee e = new Employee();
        Admin a = new Admin();
        dutyService.assignDutyByAdminToManager(duty, e.getId(), a.getId());
        return ResponseEntity.ok("Duty assigned to employee Successfully");

    }

    @GetMapping("/viewallleaveApplications")
    public ResponseEntity<List<Leave>> viewAllLeaveApplications(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token).get("role").equals("ADMIN")) {
            return ResponseEntity.status(403).body(null);

        }
        List<Leave> leaves = adminService.viewAllLeaveApplications();
        return ResponseEntity.ok(leaves);


    }

    @DeleteMapping("/deleteemployee")
    public ResponseEntity<String> deleteEmployee(@RequestParam Long eid, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token).get("role").equals("ADMIN")) {
            return ResponseEntity.status(403).body(null);


        }
        String res = adminService.deleteEmployee(eid);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/deletemanager")
    public ResponseEntity<String> deleteManager(@RequestParam Long mid, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token).get("role").equals("ADMIN")) {
            return ResponseEntity.status(403).body(null);


        }
        String res = adminService.deleteManager(mid);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/viewemployeeduties")
    public ResponseEntity<List<Duty>> viewEmployeeAssignedDuties(@RequestParam Long eid, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token).get("role").equals("ADMIN")) {
            return ResponseEntity.status(403).body(null);


        }
        List<Duty> duties = dutyService.viewAllDutiesOfEmployee(eid);
        return ResponseEntity.ok(duties);
    }

    @GetMapping("/viewassigneddutiesbymanager")
    public ResponseEntity<List<Duty>> getDutiesAssignedByManager(@RequestParam Long mid, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token).get("role").equals("ADMIN")) {
            return ResponseEntity.status(403).body(null);


        }
        List<Duty> duties = dutyService.viewDutiesAssignedByManager(mid);
        return ResponseEntity.ok(duties);

    }
    @GetMapping("/viewassigneddutiesbyadmin")
    public ResponseEntity<List<Duty>> getDutiesAssignedByAdmin(@RequestParam Long aid, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token).get("role").equals("ADMIN")) {
            return ResponseEntity.status(403).body(null);


        }
        List<Duty> duties = dutyService.viewDutiesAssignedByManager(aid);
        return ResponseEntity.ok(duties);

    }
    @GetMapping("/managercount")
    public ResponseEntity<Long> getManagerCount(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token).get("role").equals("ADMIN")) {
            return ResponseEntity.status(403).body(null);

        }
        return ResponseEntity.ok(adminService.managercount());
    }
    @GetMapping("/employeecount")
    public ResponseEntity<Long> getEmployeeCount(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token).get("role").equals("ADMIN")) {
            return ResponseEntity.status(403).body(null);

        }
        return ResponseEntity.ok(adminService.employeecount());
    }
}