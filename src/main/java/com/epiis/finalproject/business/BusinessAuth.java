package com.epiis.finalproject.business;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class BusinessAuth {

    private final Keycloak keycloakAdminClient;

    @Value("${keycloak.target.realm}")
    private String targetRealm;

    public BusinessAuth(Keycloak keycloakAdminClient) {
        this.keycloakAdminClient = keycloakAdminClient;
    }

    public boolean sendPasswordResetEmail(String email) {
        try {
            List<UserRepresentation> users = keycloakAdminClient.realm(targetRealm).users().search(email);

            if (users != null && !users.isEmpty()) {
                String userId = users.get(0).getId();
                
                keycloakAdminClient.realm(targetRealm).users().get(userId).executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error al comunicarse con Keycloak: " + e.getMessage());
            throw new RuntimeException("Error interno del servidor de autenticación");
        }
    }
}