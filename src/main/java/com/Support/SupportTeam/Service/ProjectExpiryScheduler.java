package com.Support.SupportTeam.Service;

import com.Support.SupportTeam.Entity.Project;
import com.Support.SupportTeam.Entity.User;
import com.Support.SupportTeam.Repository.ProjectRepo;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * This Job scheduled to run every day at 9 AM <br>
 * it will send alert mail to all managers that there is a project well expire soon [ 10 days ]
 */
@AllArgsConstructor
@Service
public class ProjectExpiryScheduler {
    private final ProjectRepo projectRepository;
    private final UserService userService;
    private final EmailService emailService;


    // Send alert for projects expiring within the next 10 days
    @Scheduled(cron = "0 00 9 * * ?") // Runs every day at 9 AM
    public void checkAndSendAlertsForExpiringProjects() {
        List<Project> expiringProjects = projectRepository.findProjectsExpiringSoon();
        List<User> managers=userService.findByRole("ROLE_MANAGER");
        managers.forEach(manager ->{
            for (Project project : expiringProjects) {
                // Send alert email to the project manager or relevant recipient
                String subject = "Alert: Project Expiration Soon!";
                String message = "The project '" + project.getName() + "' is expiring soon on " + project.getEndingDate();
                String recipient = manager.getEmail();

                try {
                    emailService.sendEmail(recipient, subject, message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Alert sent for project: " + project.getName());
            }
        } );

    }
}
