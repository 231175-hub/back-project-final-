package com.epiis.finalproject.dto.response.auth;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseAuth {
    private boolean success;
    private String message;
    private String accessToken;
    private String refreshToken;
    private Map<String, Object> user = new HashMap<>();

    public ResponseAuth() {}

    public ResponseAuth(boolean success, String message, String accessToken, String refreshToken) {
        this.success = success;
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
