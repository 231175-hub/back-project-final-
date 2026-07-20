package com.epiis.finalproject.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epiis.finalproject.config.JwtService;
import com.epiis.finalproject.dto.request.auth.RequestForgotPassword;
import com.epiis.finalproject.dto.request.auth.RequestLogin;
import com.epiis.finalproject.dto.request.auth.RequestRefreshToken;
import com.epiis.finalproject.dto.response.auth.ResponseAuth;
import com.epiis.finalproject.entity.EntityUser;
import com.epiis.finalproject.repository.RepositoryUser;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "intranet/auth")
public class AuthController {

    private final RepositoryUser repositoryUser;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(RepositoryUser repositoryUser, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repositoryUser = repositoryUser;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody RequestLogin request) {
        Optional<EntityUser> optionalUser = repositoryUser.findFirstByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            ResponseAuth response = new ResponseAuth(false, "Credenciales inválidas. Usuario no encontrado.", null, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        EntityUser user = optionalUser.get();

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            ResponseAuth response = new ResponseAuth(false, "Credenciales inválidas. Contraseña incorrecta.", null, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        repositoryUser.save(user);

        ResponseAuth response = new ResponseAuth(true, "Inicio de sesión exitoso", accessToken, refreshToken);
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("idUser", user.getIdUser());
        userData.put("email", user.getEmail());
        userData.put("fullName", user.getFirstName() + " " + user.getSurName());
        userData.put("role", user.getParentRole() != null ? user.getParentRole().getNameRole() : "Sin Rol");
        userData.put("urlImageProfile", user.getUrlImageProfile());
        
        response.setUser(userData);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RequestRefreshToken request) {
        String refreshToken = request.getRefreshToken();

        try {
            if (!jwtService.isTokenValid(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseAuth(false, "Token de refresco inválido o expirado.", null, null));
            }

            String email = jwtService.extractUsername(refreshToken);
            Optional<EntityUser> optionalUser = repositoryUser.findFirstByEmail(email);

            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseAuth(false, "Usuario no encontrado.", null, null));
            }

            EntityUser user = optionalUser.get();

            if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseAuth(false, "Token de refresco no coincide o ha sido revocado.", null, null));
            }

            String newAccessToken = jwtService.generateAccessToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            user.setRefreshToken(newRefreshToken);
            repositoryUser.save(user);

            ResponseAuth response = new ResponseAuth(true, "Token renovado exitosamente", newAccessToken, newRefreshToken);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseAuth(false, "Error al procesar el token de refresco: " + e.getMessage(), null, null));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody RequestForgotPassword request) {
        Optional<EntityUser> optionalUser = repositoryUser.findFirstByEmail(request.getEmail());

        Map<String, Object> response = new HashMap<>();
        if (optionalUser.isPresent()) {
            response.put("success", true);
            response.put("message", "Si el correo está registrado en el sistema, recibirás instrucciones para restablecer tu contraseña.");
        } else {
            response.put("success", true); // Don't expose whether email exists for security reasons
            response.put("message", "Si el correo está registrado en el sistema, recibirás instrucciones para restablecer tu contraseña.");
        }
        return ResponseEntity.ok(response);
    }
}