package com.employee.code.controller;

import com.employee.code.model.Duty;
import com.employee.code.model.Employee;
import com.employee.code.model.Leave;
import com.employee.code.security.JWTUtilizer;
import com.employee.code.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@CrossOrigin("*")

public class EmployeeController {
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
    @Autowired
    private EmployeeService employeeService ;

    private boolean isAuthorized(String authHeader, String expectedrole){
        try{
            String token = authHeader.substring(7);
            String role = jwtService.validateToken(token).get("role");

            return  role.equals(expectedrole);

        } catch (Exception e) {
            return  false;
        }
    }
 @PostMapping(value="/addemployee")
    public ResponseEntity<String> registerEmployee(
            @RequestParam String name,
            @RequestParam String gender,
            @RequestParam int age ,
            @RequestParam String designation,
            @RequestParam String department,
            @RequestParam double salary,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String contact){
        try{
            Employee emp = new Employee();
            emp.setName(name);
            emp.setGender(gender);
            emp.setAge(age);
            emp.setDesignation(designation);
            emp.setDepartment(department);
            emp.setSalary(salary);
            emp.setUsername(username);
            emp.setEmail(email);
            emp.setContact(contact);

            return ResponseEntity.ok(employeeService.registerEmployee(emp));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Occured");

        }

 }
 @GetMapping(".viewprofile")
    public ResponseEntity<Employee> viewEmpProfile(@RequestParam Long empid,@RequestHeader("Authorize") String authHeader){
     if(!isAuthorized(authHeader,"EMPLOYEE")){
         return ResponseEntity.status(403).body(null);

     }
   return ResponseEntity.ok(employeeService.findEmployeeById(empid));
 }
    @GetMapping(".viewduties")
    public ResponseEntity<List<Duty>> viewAssignedDuties(@RequestParam Long empid, @RequestHeader("Authorize") String authHeader){
        if(!isAuthorized(authHeader,"EMPLOYEE")){
            return ResponseEntity.status(403).body(null);

        }
        return ResponseEntity.ok(employeeService.viewAssignedDuties(empid));
    }
    @PostMapping("/applyleave")
    public ResponseEntity<Leave> applyLeave(@RequestBody Leave leave, @RequestParam Long empid, @RequestHeader ("Authorization") String authHeader){
        if(!isAuthorized(authHeader,"EMPLOYEE")){
            return ResponseEntity.status(403).body(null);

        }

        Leave l = leaveService.applyLeaveByEmployee(leave,empid);
        return ResponseEntity.ok(l);

    }
    @GetMapping("/viewownleaves")
    public ResponseEntity<List<Leave>> viewAllOwnLeaves(@RequestParam Long empid ,@RequestHeader ("Authorization") String authHeader) {
        if (!isAuthorized(authHeader, "EMPLOYEE")) {
            return ResponseEntity.status(403).body(null);

        }
        List<Leave> le = leaveService.viewLeavesByManager(empid);
        return ResponseEntity.ok(le);
    }



}
