package com.example.demo;

import com.example.demo.dto.LoginUserDto;
import com.example.demo.dto.RegisterUserDto;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.RoleEnum;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

class AuthenticationServiceTest {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        authenticationService = new AuthenticationService(
            userRepository, roleRepository, authenticationManager, passwordEncoder
        );
    }

    @Test
    void testRegister() {
        // Arrange
        RegisterUserDto registerUserDto = new RegisterUserDto(
                "john.doe@example.com" ,  "password","John Doe"
        );
        Role userRole = new Role(1L, RoleEnum.USER, "test", LocalDateTime.now(),
                LocalDateTime.now());
        Mockito.when(roleRepository.findByName(RoleEnum.USER))
            .thenReturn(Optional.of(userRole));
        Mockito.when(passwordEncoder.encode(Mockito.anyString()))
            .thenReturn("encoded_password");


        User registeredUser = authenticationService.register(registerUserDto);


        Assertions.assertNotNull(registeredUser);
        Assertions.assertEquals(registerUserDto.getFullName(), registeredUser.getFullName());
        Assertions.assertEquals(registerUserDto.getEmail(), registeredUser.getEmail());
        Assertions.assertEquals("encoded_password", registeredUser.getPassword());
        Assertions.assertEquals(userRole, registeredUser.getRole());
    }

    @Test
    void testAuthenticate() {

        LoginUserDto loginUserDto = new LoginUserDto("john.doe@example.com", "password");
        User user = new User(1L, "John Doe", "john.doe@example.com",
                "encoded_password", null, null,

                new Role(1L, RoleEnum.USER, null, null, null));
        Mockito.when(userRepository.findByEmail(loginUserDto.getEmail()))
            .thenReturn(Optional.of(user));
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(Mockito.mock(Authentication.class));


        User authenticatedUser = authenticationService.authenticate(loginUserDto);


        Assertions.assertNotNull(authenticatedUser);
        Assertions.assertEquals(user.getId(), authenticatedUser.getId());
        Assertions.assertEquals(user.getFullName(), authenticatedUser.getFullName());
        Assertions.assertEquals(user.getEmail(), authenticatedUser.getEmail());
        Assertions.assertEquals(user.getPassword(), authenticatedUser.getPassword());
        Assertions.assertEquals(user.getRole(), authenticatedUser.getRole());
    }
}