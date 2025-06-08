package com.employee.code.services;

import com.employee.code.model.*;
import com.employee.code.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AdminServiceImplementation implements AdminService {



    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private EmailRepository emailRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private LeaveRepository leaveRepository;

    @Override
    public Admin checkadminlogin(String username, String password) {
      return adminRepository.findByUsernameAndPassword(username,password);

    }

    @Override
    public Manager addManager(Manager manager) {
       Long manager_id = generateRamdomManagerId();
       String randomPassword = generateRandomPassword(8);
       manager.setId(manager_id);
       manager.setPassword(randomPassword);

       Manager savedManager = managerRepository.save(manager);
       Email e = new Email();
       e.setRecipient(manager.getEmail());
       e.setSubject("Welcome Manager");

       e.setMessage("Hi" + manager.getName() + "\n\n Successfully added \n\n" + "here is your username:" + manager.getUsername() +"/n Password:" + manager.getPassword());
       emailRepository.save(e);

       emailService.sendEmail(e.getRecipient(),e.getSubject(),e.getMessage());
       return savedManager;

       }

    @Override
    public List<Manager> viewAllManagers() {

        return managerRepository.findAll();
    }

    @Override
    public String deleteManager(long mid) {

        Optional<Manager> manager = managerRepository.findById(mid);
        if(manager.isPresent()){
            managerRepository.deleteById(mid);
            return "Manager delete success";
        }
        else{
            return "Manager id not found";
        }
    }

    @Override
    public List<Employee> viewAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public String deleteEmployee(long eid) {
        Optional<Employee> employee = employeeRepository.findById(eid);
        if(employee.isPresent()){
            employeeRepository.deleteById(eid);
            return "Employee delete success";
        }
        else{
            return "Employee id not found";
        }
    }

    @Override
    public long managercount() {
        return managerRepository.count();
    }

    @Override
    public long employeecount() {
        return employeeRepository.count();

    }


    @Override
    public List<Leave> viewAllLeaveApplications() {
        return leaveRepository.findAll();
    }
    private  long generateRamdomManagerId(){
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
    }

