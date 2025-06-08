package com.employee.code.services;

import com.employee.code.model.Email;
import com.employee.code.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private EmailRepository emailRepository;

    public void sendEmail(String to,String subject,String message){
        Email e = new Email();
        e.setRecipient(to);
        e.setRecipient(subject);
        e.setRecipient(message);
        e.setSentAt(LocalDateTime.now());

        try {
            SimpleMailMessage messager = new SimpleMailMessage();
            messager.setTo(to);
            messager.setSubject(subject);
            messager.setText(message);

            mailSender.send(messager);
            e.setStatus("SUCCESS");
        }
        catch (Exception ex){
            e.setStatus("FAILURE");
            System.out.println(ex.getMessage());
        }
        emailRepository.save(e);

    }
    public  void sendResetLink(String toEmail,String resetLink){
        String subject = "Password request link";
        String body = "Click the below link to reset your password:\n" + resetLink +"\n\n Please ignore, if not requested.";
        sendEmail(toEmail,subject,body);
    }
}
