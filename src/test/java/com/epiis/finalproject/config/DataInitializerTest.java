package com.epiis.finalproject.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.epiis.finalproject.entity.EntityRole;
import com.epiis.finalproject.entity.EntityUser;
import com.epiis.finalproject.repository.RepositoryRole;
import com.epiis.finalproject.repository.RepositoryUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataInitializerTest {

    private RepositoryRole repositoryRole;
    private RepositoryUser repositoryUser;
    private PasswordEncoder passwordEncoder;
    private JdbcTemplate jdbcTemplate;
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() throws Exception {
        repositoryRole = mock(RepositoryRole.class);
        repositoryUser = mock(RepositoryUser.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jdbcTemplate = mock(JdbcTemplate.class);
        dataInitializer = new DataInitializer();

        setField("defaultUserPassword", "defaultPass");
        setField("masterPassword", "masterPass");

        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
    }

    private void setField(String name, String value) throws Exception {
        Field field = DataInitializer.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(dataInitializer, value);
    }

    private void runInitializer() throws Exception {
        CommandLineRunner runner = dataInitializer.initDatabase(repositoryRole, repositoryUser, passwordEncoder, jdbcTemplate);
        runner.run();
    }

    private EntityRole role(String name) {
        EntityRole role = new EntityRole();
        role.setNameRole(name);
        return role;
    }

    @Test
    void testInitSeedsEverythingWhenDatabaseIsEmpty() throws Exception {
        when(repositoryRole.findAll()).thenReturn(new ArrayList<>());
        when(repositoryUser.findAll()).thenReturn(new ArrayList<>());

        runInitializer();

        verify(repositoryRole, times(4)).save(any(EntityRole.class));
        verify(repositoryUser, times(1)).save(any(EntityUser.class));
    }

    @Test
    void testInitSkipsCreationWhenRolesAndAdminExist() throws Exception {
        when(repositoryRole.findAll()).thenReturn(List.of(role("ADMIN"), role("PROFESSOR"), role("STUDENT")));

        EntityUser master = new EntityUser();
        master.setEmail("admin@master.edu.pe");
        when(repositoryUser.findAll()).thenReturn(List.of(master));

        runInitializer();

        verify(repositoryRole, never()).save(any(EntityRole.class));
        verify(repositoryUser, times(1)).save(master);
        assertEquals("hashed", master.getPassword());
    }

    @Test
    void testInitIgnoresSqlAndSeedErrors() throws Exception {
        doThrow(new RuntimeException("boom")).when(jdbcTemplate).execute(anyString());
        when(repositoryRole.findAll()).thenReturn(new ArrayList<>());
        when(repositoryUser.findAll()).thenReturn(new ArrayList<>());

        runInitializer();

        verify(repositoryUser, times(1)).save(any(EntityUser.class));
    }
}
