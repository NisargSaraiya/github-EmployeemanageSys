package com.employee.code.controller;

import com.employee.code.dto.LoginRequest;
import com.employee.code.model.Admin;
import com.employee.code.model.Employee;
import com.employee.code.model.Manager;
import com.employee.code.security.JWTUtilizer;
import com.employee.code.services.AdminService;
import com.employee.code.services.EmployeeService;
import com.employee.code.services.ManagerService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import com.employee.code.dto.AdminDTO;
import com.employee.code.dto.ManagerDTO;
import com.employee.code.dto.EmployeeDTO;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:2027")
public class AuthController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private JWTUtilizer jwtService;
    @GetMapping("/test")
    public String test() {

        return "AuthController is active!";
    }

    @GetMapping("/")
    public String home(){
    return "Employee Management System Backend is running";
}
@PostMapping("/checklogin")
public ResponseEntity<?> login(@RequestBody LoginRequest loginrequest) {
    String identifier = loginrequest.getIdentifier();
    String password = loginrequest.getPassword();
    System.out.println("[AuthController] Login attempt: identifier >" + identifier + "<, password >" + password + "<");

    Admin admin = adminService.checkadminlogin(identifier, password);
    Manager manager = managerService.checkmanagerlogin(identifier, password);
    Employee employee = employeeService.checkemployeelogin(identifier, password);

    if (admin != null) {
        String token = jwtService.generateJWTToken(admin.getUsername(), "ADMIN");
        Map<String, Object> res = new HashMap<>();
        res.put("role", "admin");
        res.put("message", "Login Successful");
        res.put("token", token);
        // Map Admin to AdminDTO
        AdminDTO dto = new AdminDTO();
        dto.setId(admin.getId());
        dto.setUsername(admin.getUsername());
        dto.setEmail(admin.getEmail());
        dto.setRole("admin");
        res.put("data", dto);
        return ResponseEntity.ok(res);
    }
    if (manager != null) {
        if (manager.getAccountstatus().equalsIgnoreCase("ACCEPTED")) {
            // Pass managerId to JWT for managers
            String token = jwtService.generateJWTToken(manager.getUsername(), "MANAGER", manager.getId());
            Map<String, Object> res = new HashMap<>();
            res.put("role", "manager");
            res.put("message", "Login Successful");
            res.put("token", token);
            ManagerDTO dto = new ManagerDTO();
            dto.setId(manager.getId());
            dto.setName(manager.getName());
            dto.setUsername(manager.getUsername());
            dto.setEmail(manager.getEmail());
            dto.setRole("manager");
            res.put("data", dto);
            return ResponseEntity.ok(res);
        } else {
            Map<String, Object> res = new HashMap<>();
            res.put("message", "Account Not accepted yet. Please Contact Admins. Status: " + manager.getAccountstatus());
            return ResponseEntity.status(401).body(res);
        }
    }
    if (employee != null) {
        if (employee.getAccountstatus() != null && employee.getAccountstatus().equalsIgnoreCase("Accepted")) {
            String token = jwtService.generateJWTToken(employee.getUsername(), "EMPLOYEE");
            Map<String, Object> res = new HashMap<>();
            res.put("role", "employee");
            res.put("message", "Login Successful");
            res.put("token", token);
            // Map Employee to EmployeeDTO
            EmployeeDTO dto = new EmployeeDTO();
            dto.setId(employee.getId());
            dto.setName(employee.getName());
            dto.setUsername(employee.getUsername());
            dto.setEmail(employee.getEmail());
            dto.setRole("employee");
            res.put("data", dto);
            return ResponseEntity.ok(res);
        } else {
            Map<String, Object> res = new HashMap<>();
            res.put("message", "Account Not accepted yet. Please Contact Admins. Status: " + employee.getAccountstatus());
            return ResponseEntity.status(401).body(res);
        }
    }

    Map<String, Object> res = new HashMap<>();
    res.put("message", "Invalid username/email or password");
    return ResponseEntity.status(401).body(res);
    }
}
