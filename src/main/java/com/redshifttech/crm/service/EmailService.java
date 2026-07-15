package com.redshifttech.crm.service;

import com.redshifttech.crm.enums.UserRole;
import com.redshifttech.crm.exception.EmailDeliveryException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRegistrationSuccessEmail(String toEmail, String firstName, UserRole role) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Registration Successful - CRM Portal");
            String htmlContent =
                    "<html>" +
                            "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                            "<h2 style='color: #0d6efd;'>Registration Successful</h2>" +
                            "<p>Hi <b>" + firstName + "</b>,</p>" +
                            "<p>Your account has been registered successfully in the <b>CRM Portal</b>.</p>" +
                            "<p><b>Role:</b> " + role + "</p>" +
                            "<p>You can now login using your registered email address.</p>" +
                            "<br>" +
                            "<p>Thank you,<br><b>CRM Team</b></p>" +
                            "</body>" +
                            "</html>";
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new EmailDeliveryException("Registration email could not be sent", e);
        }
    }

    public void sendForgotPasswordOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Password Reset OTP - CRM Portal");
            String htmlContent =
                    "<html>" +
                            "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                            "<h2 style='color: #0d6efd;'>Password Reset Request</h2>" +
                            "<p>Hello,</p>" +
                            "<p>You requested to reset your CRM Portal password.</p>" +
                            "<p>Your OTP is:</p>" +
                            "<h1 style='letter-spacing: 4px; color: #0d6efd;'>" + otp + "</h1>" +
                            "<p>This OTP is valid for 10 minutes.</p>" +
                            "<p>If you did not request this, please ignore this email.</p>" +
                            "<br>" +
                            "<p>Thank you,<br><b>CRM Team</b></p>" +
                            "</body>" +
                            "</html>";
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new EmailDeliveryException("Password reset email could not be sent", e);
        }
    }
}
