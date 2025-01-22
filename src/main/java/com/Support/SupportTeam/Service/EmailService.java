package com.Support.SupportTeam.Service;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    @Async
    public void sendEmail(String To,String Subject,String Body){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom("******@gmail.com");  // But your Mail or any Mail you need to send from
        message.setTo(To);
        message.setSubject(Subject);
        message.setText(Body);


        mailSender.send(message);
        System.out.println("Mail Sent successfully...");
    }





}
