package com.kms.cntt.service.impl;

import com.kms.cntt.common.CommonFunction;
import com.kms.cntt.exception.ForbiddenException;
import com.kms.cntt.model.OauthAccessToken;
import com.kms.cntt.model.User;
import com.kms.cntt.repository.OauthAccessTokenRepository;
import com.kms.cntt.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OauthAccessTokenServiceImpTest {

    @Mock
    private UserService userService;

    @Mock
    private OauthAccessTokenRepository oauthAccessTokenRepository;

    @InjectMocks
    private OauthAccessTokenServiceImp oauthAccessTokenService;


    @Test
    @DisplayName("Should return the oauthaccesstoken when the refresh token is valid")
    void getOauthAccessTokenByRefreshTokenWhenRefreshTokenIsValidThenReturnOauthAccessToken() {
        UUID refreshToken = UUID.randomUUID();
        OauthAccessToken oauthAccessToken = new OauthAccessToken();
        oauthAccessToken.setRefreshToken(refreshToken);
        when(oauthAccessTokenRepository.findByRefreshToken(refreshToken))
                .thenReturn(java.util.Optional.of(oauthAccessToken));

        OauthAccessToken result =
                oauthAccessTokenService.getOauthAccessTokenByRefreshToken(refreshToken);

        assertEquals(oauthAccessToken.getRefreshToken(), result);
    }

    @Test
    @DisplayName("Should throw an exception when the refresh token is revoked")
    void getOauthAccessTokenByRefreshTokenWhenRefreshTokenIsRevokedThenThrowException() {
        UUID refreshToken = UUID.randomUUID();
        OauthAccessToken oauthAccessToken = new OauthAccessToken();
        oauthAccessToken.setRevokedAt(CommonFunction.getCurrentDateTime());
        when(oauthAccessTokenRepository.findByRefreshToken(refreshToken))
                .thenReturn(java.util.Optional.of(oauthAccessToken));

        ForbiddenException exception =
                assertThrows(
                        ForbiddenException.class,
                        () -> {
                            oauthAccessTokenService.getOauthAccessTokenByRefreshToken(refreshToken);
                        });

        assertEquals("revoked_token", exception.getMessage());
    }

    @Test
    @DisplayName("Should return the token when the token is valid")
    void getOauthAccessTokenByIdWhenTokenIsValid() {
        UUID id = UUID.randomUUID();
        OauthAccessToken oauthAccessToken = new OauthAccessToken();
        oauthAccessToken.setId(id);
        oauthAccessToken.setUser(new User());

        when(oauthAccessTokenRepository.findById(any(UUID.class)))
                .thenReturn(java.util.Optional.of(oauthAccessToken));

        assertEquals(oauthAccessToken, oauthAccessTokenService.getOauthAccessTokenById(id));
    }

    @Test
    @DisplayName("Should return a new token when the refresh token is valid")
    void createTokenWhenRefreshTokenIsValidThenReturnNewToken() {
        UUID userId = UUID.randomUUID();
        UUID refreshToken = UUID.randomUUID();
        User user = new User();
        OauthAccessToken oauthAccessToken = new OauthAccessToken();
        oauthAccessToken.setUser(user);
        oauthAccessToken.setRefreshToken(refreshToken);

        when(userService.findById(any(UUID.class))).thenReturn(user);
        when(oauthAccessTokenRepository.save(any(OauthAccessToken.class)))
                .thenReturn(oauthAccessToken);

        OauthAccessToken result = oauthAccessTokenService.createToken(userId, refreshToken);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(refreshToken, result.getRefreshToken());

        verify(userService, times(1)).findById(any(UUID.class));
        verify(oauthAccessTokenRepository, times(1)).save(any(OauthAccessToken.class));
    }
}