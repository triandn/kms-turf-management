package com.kms.cntt.service.impl;

import com.kms.cntt.common.CommonFunction;
import com.kms.cntt.common.MessageConstant;
import com.kms.cntt.exception.ForbiddenException;
import com.kms.cntt.model.OauthAccessToken;
import com.kms.cntt.repository.OauthAccessTokenRepository;
import com.kms.cntt.service.OauthAccessTokenService;
import com.kms.cntt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthAccessTokenServiceImp implements OauthAccessTokenService {

  private final UserService userService;
  private final OauthAccessTokenRepository oauthAccessTokenRepository;

  @Override
  public OauthAccessToken createToken(UUID userId, UUID refreshToken) {
    try {
      OauthAccessToken oauthAccessToken = new OauthAccessToken();
      oauthAccessToken.setCreatedAt(CommonFunction.getCurrentDateTime());
      oauthAccessToken.setUser(userService.findById(userId));
      oauthAccessToken.setRefreshToken(
          Objects.requireNonNullElseGet(refreshToken, UUID::randomUUID));
      return oauthAccessTokenRepository.save(oauthAccessToken);
    } catch (Exception e) {
      log.error("INVALID_REFRESH_TOKEN  ----------------- " + e.getMessage());
      throw new ForbiddenException(MessageConstant.INVALID_REFRESH_TOKEN);
    }
  }

  @Override
  public OauthAccessToken getOauthAccessTokenById(UUID id) {
    return oauthAccessTokenRepository
        .findById(id)
        .orElseThrow(() -> new ForbiddenException(MessageConstant.INVALID_TOKEN));
  }

  @Override
  public OauthAccessToken getOauthAccessTokenByRefreshToken(UUID id) {
    OauthAccessToken oauthAccessToken =
        oauthAccessTokenRepository
            .findByRefreshToken(id)
            .orElseThrow(() -> new ForbiddenException(MessageConstant.INVALID_REFRESH_TOKEN));
    if (oauthAccessToken.getRevokedAt() != null) {
      throw new ForbiddenException(MessageConstant.REVOKED_TOKEN);
    }
    oauthAccessToken.setRevokedAt(CommonFunction.getCurrentDateTime());
    oauthAccessToken.setRefreshToken(null);
    return oauthAccessTokenRepository.save(oauthAccessToken);
  }

  @Override
  public void revoke(OauthAccessToken oauthAccessToken) {
    oauthAccessToken.setRevokedAt(CommonFunction.getCurrentDateTime());
    oauthAccessTokenRepository.save(oauthAccessToken);
  }
}
