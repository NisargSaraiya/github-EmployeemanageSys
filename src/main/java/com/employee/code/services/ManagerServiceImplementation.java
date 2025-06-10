package com.employee.code.services;

import com.employee.code.model.Employee;
import com.employee.code.model.Manager;
import com.employee.code.model.ResetToken;
import com.employee.code.repository.EmployeeRepository;
import com.employee.code.repository.ManagerRepository;
import com.employee.code.repository.ResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManagerServiceImplementation implements ManagerService{

    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ResetTokenRepository resetTokenRepository;
    @Override
    public Manager checkmanagerlogin(String identifier, String password) {
        return managerRepository.findByUsernameOrEmailAndPassword(identifier, password);

    }

    @Override
    public Manager findManagerById(long id) {
        Optional<Manager> manager = managerRepository.findById(id);
        return manager.orElse(null);
    }

    @Override
    public Manager findManagerByUsername(String username) {
        return managerRepository.findByUsername(username).orElse(null);
    }



    @Override
    public Manager findManagerByEmail(String email) {
        return managerRepository.findByEmail(email).orElse(null);
    }

    @Override
    public List<Manager> viewAllManagers() {
        return managerRepository.findAll();
    }

    @Override
    public List<Employee> viewAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public List<Employee> findEmployeesByManagerId(Long managerId) {
        return employeeRepository.findByManagerId(managerId);
    }

    @Override
    public String updateEmployeeAccountStatus(Long employeeid, String status) {
        Optional<Employee> emp = employeeRepository.findById(employeeid);
        if (emp.isPresent()) {
            Employee existingEmployee = emp.get();
            existingEmployee.setAccountstatus(status);
            employeeRepository.save(existingEmployee);
            return "Employee Account Status Updated Successfully";
        } else {
            return "Employee ID Not Found";
        }
    }

    @Override
    public String generateResetToken(String email) {
        Optional<Manager> manager = managerRepository.findByEmail(email);
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
    public boolean changePassword(Manager manager, String oldPassword, String newPassword) {
        if(manager.getPassword().equals(oldPassword)){
            manager.setPassword((newPassword));
            managerRepository.save(manager);
            return true;
        }
        return  false;
    }

    @Override
    public void updatePassword(String token, String newPassword) {
      Optional<ResetToken> resetToken = resetTokenRepository.findByToken(token);
      if(resetToken.isPresent()&& !isTokenExpired(token)){
          Manager m = new Manager();
          m.setPassword(newPassword);
          managerRepository.save(m);
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

    @Override
    public void updateManager(Manager manager) {
        managerRepository.save(manager);
    }
}
