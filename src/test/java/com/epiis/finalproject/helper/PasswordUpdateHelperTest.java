package com.epiis.finalproject.helper;

import com.epiis.finalproject.dto.request.user.RequestUserUpdatePassword;
import com.epiis.finalproject.dto.response.user.ResponseUserUpdatePassword;
import com.epiis.finalproject.entity.EntityUser;
import com.epiis.finalproject.repository.RepositoryUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordUpdateHelperTest {

    private RepositoryUser repositoryUser;
    private PasswordEncoder passwordEncoder;
    private PasswordUpdateHelper passwordUpdateHelper;

    @BeforeEach
    void setUp() {
        repositoryUser = mock(RepositoryUser.class);
        passwordEncoder = mock(PasswordEncoder.class);
        passwordUpdateHelper = new PasswordUpdateHelper(repositoryUser, passwordEncoder);
    }

    @Test
    void testUpdatePasswordSuccess() {
        EntityUser user = new EntityUser();
        user.setEmail("test@test.com");
        when(repositoryUser.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNewpass");

        RequestUserUpdatePassword req = new RequestUserUpdatePassword();
        req.setPassword("newpass");

        ResponseUserUpdatePassword response = passwordUpdateHelper.updatePassword(
                "test@test.com", req, "OK", "FAIL");

        assertEquals("success", response.getType());
        assertEquals("OK", response.getListMessage().get(0));
        verify(repositoryUser, times(1)).save(user);
    }

    @Test
    void testUpdatePasswordUserNotFound() {
        when(repositoryUser.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        RequestUserUpdatePassword req = new RequestUserUpdatePassword();
        req.setPassword("newpass");

        ResponseUserUpdatePassword response = passwordUpdateHelper.updatePassword(
                "notfound@test.com", req, "OK", "FAIL");

        assertEquals("error", response.getType());
        assertEquals("FAIL", response.getListMessage().get(0));
        verify(repositoryUser, never()).save(any());
    }
}
