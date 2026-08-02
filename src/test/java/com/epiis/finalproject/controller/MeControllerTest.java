package com.epiis.finalproject.controller;

import com.epiis.finalproject.business.BusinessUser;
import com.epiis.finalproject.entity.EntityRole;
import com.epiis.finalproject.entity.EntityUser;
import com.epiis.finalproject.repository.RepositoryUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MeControllerTest {

    private RepositoryUser repositoryUser;
    private BusinessUser businessUser;
    private MeController meController;

    @BeforeEach
    void setUp() {
        repositoryUser = mock(RepositoryUser.class);
        businessUser = mock(BusinessUser.class);
        meController = new MeController(repositoryUser, businessUser);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testObtainMyProfileUnauthenticated() {
        Map<String, Object> result = meController.obtainMyProfile();
        assertEquals("Usuario no autenticado", result.get("error"));
    }

    @Test
    void testObtainMyProfileUserFound() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "user@test.com", "pass", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        EntityUser user = new EntityUser();
        user.setIdUser("u1");
        user.setEmail("user@test.com");
        user.setFirstName("Juan");
        user.setSurName("Perez");

        EntityRole role = new EntityRole();
        role.setNameRole("ADMIN");
        user.setParentRole(role);

        when(repositoryUser.findFirstByEmail("user@test.com")).thenReturn(Optional.of(user));

        Map<String, Object> result = meController.obtainMyProfile();
        assertEquals("u1", result.get("id_user"));
        assertEquals("user@test.com", result.get("email"));
        assertEquals("Juan Perez", result.get("full_name"));
        assertEquals("ADMIN", result.get("role"));
    }

    @Test
    void testObtainMyProfileUserNotFound() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "unknown@test.com", "pass", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(repositoryUser.findFirstByEmail("unknown@test.com")).thenReturn(Optional.empty());
        when(repositoryUser.findById("unknown@test.com")).thenReturn(Optional.empty());

        Map<String, Object> result = meController.obtainMyProfile();
        assertEquals("No se pudo cargar el perfil local del usuario.", result.get("error"));
    }

    @Test
    void testServeProfileImage() {
        Resource resource = new ByteArrayResource("image data".getBytes());
        when(businessUser.getProfilePictureResource("avatar.jpg")).thenReturn(resource);

        ResponseEntity<Resource> response = meController.serveProfileImage("avatar.jpg");
        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
    }
}
