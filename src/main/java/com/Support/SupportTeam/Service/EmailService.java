package com.Support.SupportTeam.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    @Async
    public void sendEmail(String To,String Subject,String Body) throws IOException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("mo.omar753@gmail.com");  // But your Mail or any Mail you need to send from
        helper.setTo(To);
        helper.setSubject(Subject);
        helper.setText(Body,true);
        mailSender.send(message);
        System.out.println("Mail Sent successfully...");
    }

    public String loadSupportUserEmailTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/support-user-email-template.html");
        return readTemplate(resource);
    }

    public String loadManagerEmailTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/manager-email.template.html");
      return   readTemplate(resource);
    }

    public String loadRegisterManagerEmailTemplate() {
        ClassPathResource resource = new ClassPathResource("templates/Register-manager.template.html");
        return   readTemplate(resource);
    }


    private String readTemplate(ClassPathResource resource){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
