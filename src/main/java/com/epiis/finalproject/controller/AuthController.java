package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessAuth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/intranet/auth")
public class AuthController {

    private final BusinessAuth businessAuth;

    public AuthController(BusinessAuth businessAuth) {
        this.businessAuth = businessAuth;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            boolean success = businessAuth.sendPasswordResetEmail(email);
            
            if (success) {
                return ResponseEntity.ok("Enlace de recuperación enviado. Revisa tu bandeja de entrada.");
            } else {
                return ResponseEntity.badRequest().body("No se encontró ningún usuario con ese correo.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al procesar la solicitud.");
        }
    }
}