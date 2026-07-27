package com.epiis.finalproject.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetPasswordEmail(String toEmail, String token) {
        String resetUrl = "http://localhost:4200/reset-password?token=" + token;

        log.info("DEVELOPMENT LINK - Password Reset URL for {}: {}", toEmail, resetUrl);
        System.out.println("=========================================================");
        System.out.println("DEVELOPMENT LINK: Password Reset URL for " + toEmail);
        System.out.println(resetUrl);
        System.out.println("=========================================================");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Restablecer tu contraseña - Intranet UNAMBA");
            message.setText("Hola,\n\n"
                    + "Hemos recibido una solicitud para restablecer la contraseña de tu cuenta.\n"
                    + "Haz clic en el siguiente enlace para restablecerla:\n\n"
                    + resetUrl + "\n\n"
                    + "Este enlace expirará en 15 minutos.\n"
                    + "Si no solicitaste este cambio, por favor ignora este correo.\n\n"
                    + "Atentamente,\n"
                    + "Soporte Intranet UNAMBA");

            mailSender.send(message);
            log.info("Reset password email successfully sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}. Error: {}. Make sure application.properties SMTP configurations are correct.", toEmail, e.getMessage());
        }
    }
}
