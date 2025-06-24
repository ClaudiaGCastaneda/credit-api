package com.aplazo.bnpl.credit.api.credit_api.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.aplazo.bnpl.credit.api.credit_api.entities.Role;
import com.aplazo.bnpl.credit.api.credit_api.entities.User;
import com.aplazo.bnpl.credit.api.credit_api.repositories.RoleRepository;
import com.aplazo.bnpl.credit.api.credit_api.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnUsers() {
        User user1 = new User();
        User user2 = new User();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> users = userService.findAll();

        assertEquals(2, users.size());
        verify(userRepository).findAll();
    }

    @Test
    void save_shouldAssignRoleUserAndEncodePassword() {
        User user = new User();
        user.setAdmin(false);
        user.setPassword("plainPassword");

        Role roleUser = new Role();
        roleUser.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User savedUser = userService.save(user);

        assertNotNull(savedUser.getRoles());
        assertEquals(1, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().contains(roleUser));
        assertEquals("encodedPassword", savedUser.getPassword());

        verify(roleRepository).findByName("ROLE_USER");
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(savedUser);
    }

    @Test
    void save_shouldAssignRoleUserAndRoleAdmin_whenUserIsAdmin() {
        User user = new User();
        user.setAdmin(true);
        user.setPassword("adminPass");

        Role roleUser = new Role();
        roleUser.setName("ROLE_USER");
        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(roleAdmin));
        when(passwordEncoder.encode("adminPass")).thenReturn("encodedAdminPass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User savedUser = userService.save(user);

        assertNotNull(savedUser.getRoles());
        assertEquals(2, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().contains(roleUser));
        assertTrue(savedUser.getRoles().contains(roleAdmin));
        assertEquals("encodedAdminPass", savedUser.getPassword());

        verify(roleRepository).findByName("ROLE_USER");
        verify(roleRepository).findByName("ROLE_ADMIN");
        verify(passwordEncoder).encode("adminPass");
        verify(userRepository).save(savedUser);
    }

    @Test
    void existsByUsername_shouldReturnTrue() {
        when(userRepository.existsByUsername("username")).thenReturn(true);

        boolean exists = userService.existsByUsername("username");

        assertTrue(exists);
        verify(userRepository).existsByUsername("username");
    }

    @Test
    void existsByUsername_shouldReturnFalse() {
        when(userRepository.existsByUsername("unknown")).thenReturn(false);

        boolean exists = userService.existsByUsername("unknown");

        assertFalse(exists);
        verify(userRepository).existsByUsername("unknown");
    }
}
