package com.employee.code.controller;

import com.employee.code.model.Duty;
import com.employee.code.model.Employee;
import com.employee.code.model.Leave;
import com.employee.code.security.JWTUtilizer;
import com.employee.code.services.AdminService;
import com.employee.code.services.DutyService;
import com.employee.code.services.LeaveService;
import com.employee.code.services.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<Employee>> viewAllEmployees(@RequestHeader ("Authorization") String authHeader){
        if(!isAuthorized(authHeader,"MANAGER")){
            return ResponseEntity.status(403).body(null);

        }

        return ResponseEntity.ok(managerService.viewAllEmployees());
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

}
