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

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
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
public ResponseEntity<?>login(@RequestBody LoginRequest loginrequest){
    String identifier = loginrequest.getIdentifier();
    String password = loginrequest.getPassword();

    Admin admin = adminService.checkadminlogin(identifier,password);
    Manager manager = managerService.checkmanagerlogin(identifier,password);
    Employee employee = employeeService.checkemployeelogin(identifier,password);

    if(admin !=null){
        String token = jwtService.generateJWTToken(admin.getUsername(),"ADMIN");
        Map<String,Object> res = new HashMap<String,Object>();
        res.put("role","admin");
        res.put("message","Login Succesful");
        res.put("token",token);
        res.put("data",admin);

        return ResponseEntity.ok(res);
    }
    if(manager!=null){
        String token = jwtService.generateJWTToken(manager.getUsername(),"MANAGER");
        Map<String,Object> res = new HashMap<String,Object>();
        res.put("role","manager");
        res.put("message","Login Succesful");
        res.put("token",token);
        res.put("data",manager);

        return ResponseEntity.ok(res);

    }
    if(employee!=null) {
        if (employee.getAccountstatus().equalsIgnoreCase("Accepted")){
            String token = jwtService.generateJWTToken(employee.getUsername(),"EMPLOYEE");
            Map<String,Object> res = new HashMap<String,Object>();
            res.put("role","employee");
            res.put("message","Login Succesful");
            res.put("token",token);
            res.put("data",employee);

            return ResponseEntity.ok(res);

        }
        else{
            return ResponseEntity.status(401).body(Map.of("message","Account Not approved yet Please Contact Admins" + employee.getAccountstatus()));

        }
    }
    return ResponseEntity.status(401).body(Map.of("message","invalid username/email or password"));


    }
}
