package com.kms.cntt.controller;

import com.kms.cntt.common.Common;
import com.kms.cntt.enums.Role;
import com.kms.cntt.mapper.UserMapper;
import com.kms.cntt.model.User;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.ChangePasswordRequest;
import com.kms.cntt.payload.request.UserSignUpRequest;
import com.kms.cntt.payload.response.UserResponse;
import com.kms.cntt.security.oauth2.UserPrincipal;
import com.kms.cntt.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("Should return success when the old password is correct")
    void changePasswordWhenOldPasswordIsCorrectThenReturnSuccess() {
        UUID userId = UUID.randomUUID();
        String oldPassword = "12345678";
        String newPassword = "12345678";
        String confirmNewPassword = "12345678";

        UserPrincipal userPrincipal =
                new UserPrincipal(
                        userId,
                        "atnguyen",
                        oldPassword,
                        "nguyen tri an",
                        "0702589121",
                        Role.ROLE_USER,
                        Collections.singletonList(
                                new SimpleGrantedAuthority(Role.ROLE_USER.toString())));

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(oldPassword,newPassword,confirmNewPassword);

        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        responseDataAPI.setStatus(Common.SUCCESS);

        when(userService.changePassword(userId, oldPassword, changePasswordRequest))
                .thenReturn(responseDataAPI);

        ResponseEntity<ResponseDataAPI> responseEntity =
                userController.changePassword(userPrincipal, changePasswordRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Should return the user when the user is found")
    void getUserWhenUserIsFound() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setFullName("nguyen tri an");
        user.setPhoneNumber("0702589121");
        user.setUsername("atnguyen");
        user.setRole(Role.ROLE_USER);

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);
        userResponse.setFullName("nguyen tri an");
        userResponse.setPhoneNumber("0702589121");
        userResponse.setUsername("atnguyen");
        userResponse.setRole(Role.ROLE_USER);

        when(userService.findById(userId)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);
        List<GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().toString()));
        ResponseEntity<ResponseDataAPI> responseEntity =
                userController.getUser(new UserPrincipal(user.getId(), user.getUsername(), user.getPassword(), user.getPassword(), user.getPhoneNumber(), user.getRole(), authorities));

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Common.SUCCESS, responseEntity.getBody().getStatus());
        assertEquals(userResponse, responseEntity.getBody().getData());
    }

    @Test
    @DisplayName(
            "Should return a response with status code 200 and the user when the request is valid")
    void registerUserWhenRequestIsValidThenReturnResponseWithStatusCode200AndUser() {
//        UserSignUpRequest userSignUpRequest = UserSignUpRequest.builder().build();
        UUID userId = UUID.randomUUID();
        UserSignUpRequest userSignUpRequest = new UserSignUpRequest();
        userSignUpRequest.setUsername("atnguyen");
        userSignUpRequest.setPassword("abcd123A");
        userSignUpRequest.setPasswordConfirmation("abcd123A");
        userSignUpRequest.setRole(Role.ROLE_ADMIN.toString());
        userSignUpRequest.setFullName("nguyen tri an");
        userSignUpRequest.setPhoneNumber("00702589121");
        //
        User user = new User();
        user.setId(userId);
        user.setPassword(userSignUpRequest.getPassword());
        user.setUsername(userSignUpRequest.getUsername());
        user.setFullName(userSignUpRequest.getFullName());
        user.setPhoneNumber(userSignUpRequest.getPhoneNumber());
        user.setRole(Role.ROLE_ADMIN);
        user.setResetPasswordToken(null);
        //
        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);
        userResponse.setRole(user.getRole());
        userResponse.setUsername(user.getUsername());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setFullName(user.getFullName());
        userResponse.setPhoneNumber(user.getPhoneNumber());

        when(userService.registerUser(userSignUpRequest))
                .thenReturn(
                        new ResponseEntity<>(ResponseDataAPI.success(user, null), HttpStatus.OK));
//        when(userMapper.toResponse(user)).thenReturn(userResponse);

        ResponseEntity<ResponseDataAPI> actual = userController.registerUser(userSignUpRequest);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(ResponseDataAPI.success(userResponse, null).getStatus(), actual.getBody().getStatus());
    }
}