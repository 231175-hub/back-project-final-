package com.epiis.finalproject.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestForgotPassword {
    @NotBlank(message = "El email es obligatorio")
    private String email;
}
