package com.Support.SupportTeam.ExceptionHandler;
import org.eclipse.angus.mail.util.MailConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.mail.MessagingException;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class MailSenderExceptionHandler {


        private static final Logger logger = LoggerFactory.getLogger(MailSenderExceptionHandler.class);

        // 🔴 1. Handle Authentication Failures (Wrong SMTP Credentials)
        @ExceptionHandler(MailAuthenticationException.class)
        public void handleMailAuthException(MailAuthenticationException ex) {
            logger.error("Mail authentication failed: Invalid SMTP credentials. Details: {}", ex.getMessage());
        }

        // 🔴 2. Handle Connection Issues (SMTP Server Unreachable)
        @ExceptionHandler(MailConnectException.class)
        public void handleMailConnectException(MailConnectException ex) {
            logger.error("Unable to connect to mail server. Check SMTP settings and network. Details: {}", ex.getMessage());
        }

        // 🔴 3. Handle Invalid Email Addresses
        @ExceptionHandler(MailParseException.class)
        public void handleMailParseException(MailParseException ex) {
            logger.error("Invalid email format detected. Details: {}", ex.getMessage());
        }

        // 🔴 4. Handle Mail Sending Errors (Recipient Address Doesn't Exist)
        @ExceptionHandler(MailSendException.class)
        public void handleMailSendException(MailSendException ex) {
            logger.error("Failed to send email. Details: {}", ex.getMessage());
        }

        // 🔴 5. Handle General Messaging Errors
        @ExceptionHandler(MessagingException.class)
        public void handleMessagingException(MessagingException ex) {
            logger.error("Mail server error encountered. Details: {}", ex.getMessage());
        }

        // 🔴 6. Catch-All for Any Other MailException
        @ExceptionHandler(MailException.class)
        public void handleGeneralMailException(MailException ex) {
            logger.error("Unexpected mail error occurred. Details: {}", ex.getMessage());
        }
    }



