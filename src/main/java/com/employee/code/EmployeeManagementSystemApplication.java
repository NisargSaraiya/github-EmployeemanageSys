package com.employee.code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import com.employee.code.model.Admin;
import com.employee.code.repository.AdminRepository;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.employee.code")
public class EmployeeManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeManagementSystemApplication.class, args);
		System.out.println("ems running");
	}

	@Bean
	public CommandLineRunner createDefaultAdmin(@Autowired AdminRepository adminRepository) {
		return args -> {
			if (adminRepository.count() == 0) {
				Admin admin = new Admin();
				admin.setUsername("admin");
				admin.setPassword("admin");
				admin.setEmail("admin@example.com");
				adminRepository.save(admin);
				System.out.println("Default admin created: username=admin, password=admin, email=admin@example.com");
			}
		};
	}

}
