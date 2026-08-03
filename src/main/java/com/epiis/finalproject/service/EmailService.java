package com.epiis.finalproject.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${app.resend.api-key:}")
    private String resendApiKey;

    @Value("${app.resend.sender-email:onboarding@resend.dev}")
    private String senderEmail;

    public void sendResetPasswordEmail(String toEmail, String token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        log.info("=========================================================");
        log.info("DEVELOPMENT LINK: Password Reset URL for {}", toEmail);
        log.info("{}", resetUrl);
        log.info("=========================================================");

        CompletableFuture.runAsync(() -> sendResetPasswordEmailSync(toEmail, resetUrl));
    }

    void sendResetPasswordEmailSync(String toEmail, String resetUrl) {
        if (resendApiKey == null || resendApiKey.trim().isEmpty()) {
            log.warn("Resend API Key is not configured. Email NOT sent to {}, but token is logged above.", toEmail);
            return;
        }

        try {
            String url = "https://api.resend.com/emails";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + resendApiKey.trim());

            Map<String, Object> body = new HashMap<>();
            body.put("from", senderEmail);
            body.put("to", toEmail);
            body.put("subject", "Restablecer tu contraseña - Intranet UNAMBA");

            String htmlContent = "<p>Hola,</p>"
                    + "<p>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta de la Intranet UNAMBA.</p>"
                    + "<p>Haz clic en el siguiente enlace para restablecerla:</p>"
                    + "<p><a href=\"" + resetUrl + "\" style=\"background-color: #003876; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;\">Restablecer Contraseña</a></p>"
                    + "<p>Este enlace expirará en 15 minutos.</p>"
                    + "<p>Si no solicitaste este cambio, por favor ignora este correo.</p>"
                    + "<br>"
                    + "<p>Atentamente,<br>Soporte Intranet UNAMBA</p>";
            
            body.put("html", htmlContent);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Reset password email successfully sent via Resend to {}", toEmail);
            } else {
                log.error("Failed to send email via Resend. Status: {}, Response: {}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("Failed to send email to {} via Resend. Error: {}", toEmail, e.getMessage());
        }
    }
}
