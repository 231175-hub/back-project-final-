package com.epiis.finalproject.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestRefreshToken {
    @NotBlank(message = "El token de refresco es obligatorio")
    private String refreshToken;
}
