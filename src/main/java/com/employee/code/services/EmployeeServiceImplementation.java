package com.employee.code.services;

import com.employee.code.model.Duty;
import com.employee.code.model.Employee;
import com.employee.code.model.Manager;
import com.employee.code.model.ResetToken;
import com.employee.code.repository.DutyRepository;
import com.employee.code.repository.EmployeeRepository;
import com.employee.code.repository.ResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmployeeServiceImplementation implements EmployeeService{
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DutyRepository dutyRepository;
    @Autowired
    private ResetTokenRepository resetTokenRepository;
    @Override
    public Employee checkemployeelogin(String username, String password) {
        return employeeRepository.findByUsernameandPassword(username,password);

    }
    private  long generateRamdomEmployeeId(){
        Random random = new Random();

        return 1000+ random.nextInt(9000);

    }

    private String generateRandomPassword(int length){
        String upper= "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower= "abcdefghijklmnopqrstuvwxyz";
        String digits = "1234567890";
        String special = "!@#$%&";
        String combined = upper + lower +digits+special;
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        sb.append(upper.charAt(random.nextInt(upper.length())));
        sb.append(lower.charAt(random.nextInt(lower.length())));
        sb.append(digits.charAt(random.nextInt(digits.length())));
        sb.append(special.charAt(random.nextInt(special.length())));

        for(int i = 4 ; i <length;i++) {
            sb.append(upper.charAt(random.nextInt(upper.length())));
            sb.append(lower.charAt(random.nextInt(lower.length())));
        }
        return sb.toString();
    }

    @Override
    public String registerEmployee(Employee emp) {
       Long eid = generateRamdomEmployeeId();
       emp.setId(eid);

       String randomPassword = generateRandomPassword(8);
       emp.setPassword(randomPassword);
        emp.setAccountstatus("Pending");
        emp.setRole("Employee");
        return "Employee Registered Successful";
    }


    @Override
    public String updateEmployeeProfile(Employee emp) {
        employeeRepository.save(emp);
        return "Employee updated Successfully";
    }

    @Override
    public Employee findEmployeeById(long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    @Override
    public Employee findEmployeeByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }

    @Override
    public Employee findEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    @Override
    public List<Employee> viewAllEmployee() {
        return employeeRepository.findAll();
    }

    @Override
    public String updateAccountStatus(Long empid, String status) {
        Optional<Employee> emp = employeeRepository.findById(empid) ;
        if(emp.isPresent()){
            Employee e = new Employee();
            e.setAccountstatus(status);
            employeeRepository.save(e);
            return "Status Updated to:" + status;

        }
        return "Employee ID not found";
    }

    @Override
    public List<Duty> viewAssignedDuties(Long empid) {
        Employee emp = employeeRepository.findById(empid).orElse(null);
        if(emp !=null){
            return dutyRepository.findByEmployee(emp);
        }
        return Collections.emptyList();

    }
    @Override
    public String generateResetToken(String email) {
        Optional<Manager> manager = employeeRepository.FindByEmail(email);
        if(manager.isPresent()){
            String token = UUID.randomUUID().toString();
            ResetToken rt = new ResetToken();
            rt.setToken(token);
            rt.setEmail(email);
            rt.setCreatedAt(LocalDateTime.now());
            rt.setExpiredAt(LocalDateTime.now().plusMinutes(5));
            resetTokenRepository.save(rt);
            return token;
        }
        return null;
    }

    @Override
    public boolean validateResetToken(String token) {
        Optional<ResetToken>rt = resetTokenRepository.findByToken(token);
        return rt.isPresent() && !isTokenExpired(token);
    }

    @Override
    public boolean changePassword(Employee employee, String oldPassword, String newPassword) {
        if(employee.getPassword().equals(oldPassword)){
            employee.setPassword((newPassword));
            employeeRepository.save(employee);
            return true;
        }
        return  false;
    }

    @Override
    public void updatePassword(String token, String newPassword) {
        Optional<ResetToken> resetToken = resetTokenRepository.findByToken(token);
        if(resetToken.isPresent()&& !isTokenExpired(token)){
            Employee e = new Employee();
            e.setPassword(newPassword);
            employeeRepository.save(e);
            deleteResetToken(token);
        }
    }

    @Override
    public void deleteResetToken(String token) {
        resetTokenRepository.deleteByToken(token);
    }

    @Override
    public boolean isTokenExpired(String token) {
        Optional<ResetToken> rt = resetTokenRepository.findByToken(token);
        if(rt.isPresent()){
            return rt.get().getExpiredAt().isBefore(LocalDateTime.now());
        }
        return true;
    }

}
