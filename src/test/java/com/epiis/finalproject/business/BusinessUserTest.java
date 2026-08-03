package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.user.RequestUserInsert;
import com.epiis.finalproject.dto.request.user.RequestUserUpdate;
import com.epiis.finalproject.dto.request.user.RequestUserUpdatePassword;
import com.epiis.finalproject.dto.request.user.RequestUserUpdateUploadImg;
import com.epiis.finalproject.dto.response.user.ResponseUserDeleteById;
import com.epiis.finalproject.dto.response.user.ResponseUserInsert;
import com.epiis.finalproject.dto.response.user.ResponseUserUpdate;
import com.epiis.finalproject.dto.response.user.ResponseUserUpdatePassword;
import com.epiis.finalproject.entity.EntityRole;
import com.epiis.finalproject.entity.EntityUser;
import com.epiis.finalproject.repository.RepositoryRole;
import com.epiis.finalproject.repository.RepositoryUser;
import com.epiis.finalproject.helper.PasswordUpdateHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class BusinessUserTest {

    private RepositoryUser repositoryUser;
    private RepositoryRole repositoryRole;
    private PasswordEncoder passwordEncoder;
    private PasswordUpdateHelper passwordUpdateHelper;
    private BusinessUser businessUser;

    @BeforeEach
    void setUp() {
        repositoryUser = mock(RepositoryUser.class);
        repositoryRole = mock(RepositoryRole.class);
        passwordEncoder = mock(PasswordEncoder.class);
        passwordUpdateHelper = mock(PasswordUpdateHelper.class);
        businessUser = new BusinessUser(repositoryUser, repositoryRole, passwordEncoder, passwordUpdateHelper);
    }

    @Test
    void testInsertAdminSuccess() {
        EntityRole adminRole = new EntityRole();
        adminRole.setNameRole("ADMIN");

        when(repositoryRole.findByNameRole(anyString())).thenReturn(Optional.of(adminRole));
        when(repositoryUser.findByEmail("admin@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        RequestUserInsert request = new RequestUserInsert();
        request.setFirstName("Admin");
        request.setSurName("User");
        request.setEmail("admin@test.com");
        request.setPassword("password123");

        ResponseUserInsert response = businessUser.insert(request);
        assertEquals("success", response.getType());
        verify(repositoryUser, times(1)).save(any(EntityUser.class));
    }

    @Test
    void testInsertAdminEmailAlreadyExists() {
        EntityRole adminRole = new EntityRole();
        adminRole.setNameRole("ADMIN");

        when(repositoryRole.findByNameRole("ADMIN")).thenReturn(Optional.of(adminRole));
        when(repositoryUser.findByEmail("admin@test.com")).thenReturn(Optional.of(new EntityUser()));

        RequestUserInsert request = new RequestUserInsert();
        request.setEmail("admin@test.com");

        ResponseUserInsert response = businessUser.insert(request);
        assertEquals("error", response.getType());
    }

    @Test
    void testGetAllAndGetById() {
        EntityUser adminUser = new EntityUser();
        EntityRole adminRole = new EntityRole();
        adminRole.setNameRole("Admin");
        adminUser.setParentRole(adminRole);

        when(repositoryUser.findAll()).thenReturn(List.of(adminUser));
        when(repositoryUser.findById("u1")).thenReturn(Optional.of(adminUser));

        Map<String, Object> all = businessUser.getAll();
        assertNotNull(all.get("data"));

        Map<String, Object> byId = businessUser.getById("u1");
        assertNotNull(byId.get("data"));
    }

    @Test
    void testDeleteById() {
        ResponseUserDeleteById response = businessUser.deleteById("u1");
        assertEquals("success", response.getType());
        verify(repositoryUser, times(1)).deleteById("u1");
    }

    @Test
    void testUpdateUserSuccess() {
        EntityUser user = new EntityUser();
        user.setIdUser("u1");
        when(repositoryUser.findById("u1")).thenReturn(Optional.of(user));

        RequestUserUpdate req = new RequestUserUpdate();
        req.setFirstName("NewFirst");
        req.setSurName("NewSur");
        req.setEmail("new@test.com");

        ResponseUserUpdate response = businessUser.update("u1", req);
        assertEquals("success", response.getType());
        verify(repositoryUser, times(1)).save(any(EntityUser.class));
    }

    @Test
    void testUpdateUserNotFound() {
        when(repositoryUser.findById("u1")).thenReturn(Optional.empty());

        RequestUserUpdate req = new RequestUserUpdate();
        ResponseUserUpdate response = businessUser.update("u1", req);
        assertEquals("error", response.getType());
    }

    @Test
    void testUpdatePasswordSuccess() {
        RequestUserUpdatePassword req = new RequestUserUpdatePassword();
        req.setPassword("newpass");

        ResponseUserUpdatePassword successResp = new ResponseUserUpdatePassword();
        successResp.success();
        successResp.getListMessage().add("Contraseña de usuario actualizada correctamente");

        when(passwordUpdateHelper.updatePassword(
                eq("user@test.com"), any(RequestUserUpdatePassword.class),
                eq("Contraseña de usuario actualizada correctamente"),
                eq("Contraseña de usuario no actualizada")))
                .thenReturn(successResp);

        ResponseUserUpdatePassword response = businessUser.updatePassword("user@test.com", req);
        assertEquals("success", response.getType());
    }

    @Test
    void testGetProfilePictureResourceFallback() {
        Resource resource = businessUser.getProfilePictureResource("nonexistent.jpg");
        assertNotNull(resource);
    }

    @Test
    void testInsertAdminRoleNotFound() {
        when(repositoryRole.findByNameRole(anyString())).thenReturn(Optional.empty());

        RequestUserInsert request = new RequestUserInsert();
        request.setEmail("noadmin@test.com");

        ResponseUserInsert response = businessUser.insert(request);
        assertEquals("error", response.getType());
    }

    @Test
    void testGetAllFiltersOutNonAdmins() {
        EntityUser admin = new EntityUser();
        EntityRole adminRole = new EntityRole();
        adminRole.setNameRole("Admin");
        admin.setParentRole(adminRole);

        EntityUser professor = new EntityUser();
        EntityRole professorRole = new EntityRole();
        professorRole.setNameRole("Professor");
        professor.setParentRole(professorRole);

        EntityUser noRole = new EntityUser();

        when(repositoryUser.findAll()).thenReturn(List.of(admin, professor, noRole));

        Map<String, Object> all = businessUser.getAll();
        List<EntityUser> data = (List<EntityUser>) all.get("data");
        assertEquals(1, data.size());
        assertEquals(admin, data.get(0));
    }

    @Test
    void testUpdateProfileImageSuccess() throws Exception {
        EntityUser user = new EntityUser();
        user.setIdUser("u1");
        when(repositoryUser.findById("u1")).thenReturn(Optional.of(user));

        MultipartFile file = mock(MultipartFile.class);
        java.io.File temp = java.io.File.createTempFile("avatar", ".jpg");
        temp.deleteOnExit();
        when(file.getInputStream()).thenReturn(new java.io.FileInputStream(temp));

        RequestUserUpdateUploadImg req = new RequestUserUpdateUploadImg();
        req.setFile(file);

        try {
            businessUser.updateProfileImage("u1", req);
            assertEquals("/intranet/me/image/u1.jpg", user.getUrlImageProfile());
            verify(repositoryUser, times(1)).save(user);
        } finally {
            java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get("uploads/profile/u1.jpg"));
        }
    }

    @Test
    void testUpdateProfileImageUserNotFound() {
        when(repositoryUser.findById("missing")).thenReturn(Optional.empty());

        RequestUserUpdateUploadImg req = new RequestUserUpdateUploadImg();
        assertThrows(RuntimeException.class, () -> businessUser.updateProfileImage("missing", req));
    }

    @Test
    void testGetProfilePictureResourceExisting() throws Exception {
        java.nio.file.Path dir = java.nio.file.Paths.get("uploads/profile");
        java.nio.file.Files.createDirectories(dir);
        java.nio.file.Path file = dir.resolve("avatar.jpg");
        java.nio.file.Files.write(file, new byte[]{1, 2, 3});

        try {
            Resource resource = businessUser.getProfilePictureResource("avatar.jpg");
            assertNotNull(resource);
            assertTrue(resource instanceof org.springframework.core.io.UrlResource);
        } finally {
            java.nio.file.Files.deleteIfExists(file);
        }
    }
}
