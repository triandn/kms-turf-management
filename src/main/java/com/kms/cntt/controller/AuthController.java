package com.kms.cntt.controller;

import com.kms.cntt.common.MessageConstant;
import com.kms.cntt.enums.Role;
import com.kms.cntt.exception.BadRequestException;
import com.kms.cntt.exception.UnauthorizedException;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.LoginRequest;
import com.kms.cntt.payload.request.RefreshTokenRequest;
import com.kms.cntt.security.oauth2.UserPrincipal;
import com.kms.cntt.security.token.TokenProvider;
import com.kms.cntt.service.OauthAccessTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final TokenProvider tokenProvider;

  private final OauthAccessTokenService oauthAccessTokenService;

  @PostMapping("/login")
  public ResponseEntity<ResponseDataAPI> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  loginRequest.getUsername().toLowerCase(), loginRequest.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
      if (userPrincipal.getRole().equals(Role.ROLE_ADMIN)) {
        throw new UnauthorizedException(MessageConstant.FORBIDDEN_ERROR);
      }

      return ResponseEntity.ok(
          ResponseDataAPI.success(tokenProvider.createOauthAccessToken(userPrincipal), null));
    } catch (BadCredentialsException e) {
      throw new BadRequestException(MessageConstant.INCORRECT_USERNAME_OR_PASSWORD);
    } catch (InternalAuthenticationServiceException e) {
      throw new UnauthorizedException(MessageConstant.ACCOUNT_NOT_EXISTS);
    } catch (AuthenticationException e) {
      throw new UnauthorizedException(MessageConstant.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/admin/login")
  public ResponseEntity<ResponseDataAPI> loginAdmin(@Valid @RequestBody LoginRequest loginRequest) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  loginRequest.getUsername().toLowerCase(), loginRequest.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
      if (!userPrincipal.getRole().equals(Role.ROLE_ADMIN)) {
        throw new UnauthorizedException(MessageConstant.FORBIDDEN_ERROR);
      }

      return ResponseEntity.ok(
          ResponseDataAPI.success(tokenProvider.createOauthAccessToken(userPrincipal), null));
    } catch (BadCredentialsException e) {
      throw new BadRequestException(MessageConstant.INCORRECT_USERNAME_OR_PASSWORD);
    } catch (InternalAuthenticationServiceException e) {
      throw new UnauthorizedException(MessageConstant.ACCOUNT_NOT_EXISTS);
    } catch (AuthenticationException e) {
      throw new UnauthorizedException(MessageConstant.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/refresh_tokens")
  public ResponseEntity<ResponseDataAPI> refreshTokenUser(
      @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    return ResponseEntity.ok(
        ResponseDataAPI.success(
            tokenProvider.refreshTokenOauthAccessToken(refreshTokenRequest.getRefreshToken()),
            null));
  }

  @PostMapping("/admin/refresh_tokens")
  public ResponseEntity<ResponseDataAPI> refreshTokenAdmin(
      @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    return ResponseEntity.ok(
        ResponseDataAPI.success(
            tokenProvider.refreshTokenOauthAccessToken(refreshTokenRequest.getRefreshToken()),
            null));
  }

  @PostMapping("/logout")
  public ResponseEntity<ResponseDataAPI> logoutUser(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      bearerToken = bearerToken.substring(7);
    }
    oauthAccessTokenService.revoke(tokenProvider.getOauthAccessTokenFromToken(bearerToken));
    return ResponseEntity.ok(ResponseDataAPI.success(null, null));
  }
}
