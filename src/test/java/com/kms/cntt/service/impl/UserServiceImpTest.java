package com.kms.cntt.service.impl;

import com.kms.cntt.enums.Role;
import com.kms.cntt.exception.BadRequestException;
import com.kms.cntt.exception.NotFoundException;
import com.kms.cntt.mapper.UserMapper;
import com.kms.cntt.model.User;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.ChangePasswordRequest;
import com.kms.cntt.payload.request.UserSignUpRequest;
import com.kms.cntt.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImp userService;


    @Test
    @DisplayName("Should return a user with the correct phonenumber")
    void toUserEntityShouldReturnAUserWithTheCorrectPhoneNumber() {
        String username = "username";
        String password = "password";
        String fullName = "fullName";
        String phoneNumber = "phoneNumber";
        String role = "ROLE_USER";

        UserSignUpRequest userSignUpRequest = new UserSignUpRequest();
        userSignUpRequest.setUsername(username);
        userSignUpRequest.setPassword(password);
        userSignUpRequest.setPasswordConfirmation(password);
        userSignUpRequest.setFullName(fullName);
        userSignUpRequest.setPhoneNumber(phoneNumber);
        userSignUpRequest.setRole(role);

        User user =
                (User)
                        ReflectionTestUtils.invokeMethod(
                                userService,
                                "toUserEntity",
                                userSignUpRequest.getUsername(),
                                userSignUpRequest.getPassword(),
                                userSignUpRequest.getFullName(),
                                userSignUpRequest.getPhoneNumber(),
                                userSignUpRequest.getRole());

        assertEquals(phoneNumber, user.getPhoneNumber());
    }

    @Test
    @DisplayName("Should return a user with the correct password")
    void toUserEntityShouldReturnAUserWithTheCorrectPassword() {
        String username = "username";
        String password = "password";
        String fullName = "fullName";
        String phoneNumber = "phoneNumber";
        String role = "ROLE_USER";

        UserSignUpRequest userSignUpRequest = new UserSignUpRequest();
        userSignUpRequest.setUsername(username);
        userSignUpRequest.setPassword(password);
        userSignUpRequest.setPasswordConfirmation(password);
        userSignUpRequest.setFullName(fullName);
        userSignUpRequest.setPhoneNumber(phoneNumber);
        userSignUpRequest.setRole(role);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User user =
                (User)
                        ReflectionTestUtils.invokeMethod(
                                userService,
                                "toUserEntity",
                                userSignUpRequest.getUsername(),
                                userSignUpRequest.getPassword(),
                                userSignUpRequest.getFullName(),
                                userSignUpRequest.getPhoneNumber(),
                                userSignUpRequest.getRole());

        assertEquals(username, user.getUsername());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals(fullName, user.getFullName());
        assertEquals(phoneNumber, user.getPhoneNumber());
        assertEquals(Role.valueOf(role), user.getRole());
    }

    @Test
    @DisplayName("Should return a user with the correct username")
    void toUserEntityShouldReturnAUserWithTheCorrectUsername() {
        UserSignUpRequest userSignUpRequest = new UserSignUpRequest();
        userSignUpRequest.setUsername("username");
        userSignUpRequest.setPassword("password");
        userSignUpRequest.setPasswordConfirmation("password");
        userSignUpRequest.setFullName("fullName");
        userSignUpRequest.setPhoneNumber("phoneNumber");
        userSignUpRequest.setRole(Role.ROLE_USER.toString());

        User user =
                (User)
                        ReflectionTestUtils.invokeMethod(
                                userService,
                                "toUserEntity",
                                userSignUpRequest.getUsername(),
                                userSignUpRequest.getPassword(),
                                userSignUpRequest.getFullName(),
                                userSignUpRequest.getPhoneNumber(),
                                userSignUpRequest.getRole());

        assertEquals(user.getUsername(), "username");
    }


    @Test
    @DisplayName(
            "Should return a response entity with status code 200 when the user is registered successfully")
    void registerUserWhenSuccessThenReturnResponseEntityWithStatusCode200() {
        String username = "test";
        String password = "12345678";
        String passwordConfirmation = "12345678";
        String fullName = "test";
        String phoneNumber = "0123456789";
        String role = Role.ROLE_USER.toString();

        UserSignUpRequest userSignUpRequest = new UserSignUpRequest();
        userSignUpRequest.setUsername(username);
        userSignUpRequest.setPassword(password);
        userSignUpRequest.setPasswordConfirmation(passwordConfirmation);
        userSignUpRequest.setFullName(fullName);
        userSignUpRequest.setPhoneNumber(phoneNumber);
        userSignUpRequest.setRole(role);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        ResponseEntity<ResponseDataAPI> responseEntity =
                userService.registerUser(userSignUpRequest);

        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should throw an exception when the username is already in use")
    void registerUserWhenUsernameIsAlreadyInUseThenThrowException() {
        UserSignUpRequest userSignUpRequest = new UserSignUpRequest();
        userSignUpRequest.setUsername("test");
        userSignUpRequest.setPassword("12345678");
        userSignUpRequest.setPasswordConfirmation("12345678");
        userSignUpRequest.setFullName("test");
        userSignUpRequest.setPhoneNumber("12345678");
        userSignUpRequest.setRole(Role.ROLE_USER.toString());

        when(userRepository.existsByUsername(any())).thenReturn(true);

        // Act & Assert
        assertThrows(
                BadRequestException.class,
                () -> {
                    userService.registerUser(userSignUpRequest);
                });

        verify(userRepository, times(1)).existsByUsername(any());
    }

    @Test
    @DisplayName("Should throw an exception when the user does not exist")
    void findByIdWhenUserDoesNotExistThenThrowException() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.findById(id));
    }

    @Test
    @DisplayName("Should return the user when the user exists")
    void findByIdWhenUserExists() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = userService.findById(id);

        assertEquals(id, result.getId());
    }
}