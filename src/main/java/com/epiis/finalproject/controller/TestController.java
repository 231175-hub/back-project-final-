package com.epiis.finalproject.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/prueba")
public class TestController {

    @GetMapping("/mi-perfil")
    public Map<String, Object> seeProfile(Authentication authentication) {
        Map<String , Object> response = new HashMap<>();
        response.put("message", "¡Conexión exitosa desde Angular hasta Spring Boot!");
        response.put("user", authentication.getName());
        response.put("roles", authentication.getAuthorities());
        
        return response;
    }
}
