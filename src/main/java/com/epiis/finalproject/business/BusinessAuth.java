package com.epiis.finalproject.business;

import org.springframework.stereotype.Service;

@Service
public class BusinessAuth {
    public boolean sendPasswordResetEmail() {
        // Password reset email handled by AuthController / local mail service
        return true;
    }
}