package com.kms.cntt.security.token;

import com.kms.cntt.common.Common;
import com.kms.cntt.common.MessageConstant;
import com.kms.cntt.exception.ForbiddenException;
import com.kms.cntt.mapper.OauthAccessTokenMapper;
import com.kms.cntt.model.OauthAccessToken;
import com.kms.cntt.model.User;
import com.kms.cntt.payload.response.OauthAccessTokenResponse;
import com.kms.cntt.security.oauth2.UserPrincipal;
import com.kms.cntt.service.OauthAccessTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenProvider {

  private final OauthAccessTokenService oauthAccessTokenService;

  private final OauthAccessTokenMapper oauthAccessTokenMapper;

  public OauthAccessTokenResponse createOauthAccessToken(UserPrincipal userPrincipal) {
    return this.createToken(userPrincipal.getId());
  }

  public OauthAccessTokenResponse createToken(UUID userId) {
    OauthAccessToken oauthAccessToken = oauthAccessTokenService.createToken(userId, null);
    return oauthAccessTokenMapper.toOauthAccessTokenResponse(
        oauthAccessToken,
        this.createAccessToken(oauthAccessToken),
        this.createRefreshToken(oauthAccessToken),
        Common.TOKEN_EXPIRATION / 1000);
  }

  public OauthAccessTokenResponse refreshTokenOauthAccessToken(String refreshToken) {
    this.validateRefreshToken(refreshToken, Common.REFRESH_TOKEN_SECRET);
    UUID refreshTokenId = this.getUUIDFromToken(refreshToken, Common.REFRESH_TOKEN_SECRET);

    OauthAccessToken oauthAccessToken =
        oauthAccessTokenService.getOauthAccessTokenByRefreshToken(refreshTokenId);
    OauthAccessToken result =
        oauthAccessTokenService.createToken(oauthAccessToken.getUser().getId(), refreshTokenId);
    return oauthAccessTokenMapper.toOauthAccessTokenResponse(
        oauthAccessToken,
        this.createAccessToken(result),
        refreshToken,
        Common.TOKEN_EXPIRATION / 1000);
  }

  private String createAccessToken(OauthAccessToken oauthAccessToken) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + Common.TOKEN_EXPIRATION);

    return Jwts.builder()
        .setSubject(oauthAccessToken.getId().toString())
        .setIssuedAt(new Date())
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS512, Common.TOKEN_SECRET)
        .compact();
  }

  private String createRefreshToken(OauthAccessToken oauthAccessToken) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + Common.REFRESH_TOKEN_EXPIRATION);

    return Jwts.builder()
        .setSubject(oauthAccessToken.getRefreshToken().toString())
        .setIssuedAt(new Date())
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS512, Common.REFRESH_TOKEN_SECRET)
        .compact();
  }

  public User getUserFromToken(String token) {
    return this.getOauthAccessTokenFromToken(token).getUser();
  }

  public OauthAccessToken getOauthAccessTokenFromToken(String token) {
    this.validateAccessToken(token, Common.TOKEN_SECRET);
    OauthAccessToken oauthAccessToken =
        oauthAccessTokenService.getOauthAccessTokenById(
            this.getUUIDFromToken(token, Common.TOKEN_SECRET));
    if (oauthAccessToken.getRevokedAt() != null) {
      throw new ForbiddenException(MessageConstant.REVOKED_TOKEN);
    }
    return oauthAccessToken;
  }

  private UUID getUUIDFromToken(String token, String secret) {
    Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    return UUID.fromString(claims.getSubject());
  }

  private void validateAccessToken(String authToken, String secret) {
    try {
      Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
    } catch (ExpiredJwtException ex) {
      throw new ForbiddenException(MessageConstant.EXPIRED_TOKEN);
    } catch (Exception ex) {
      throw new ForbiddenException(MessageConstant.INVALID_TOKEN);
    }
  }

  private void validateRefreshToken(String authToken, String secret) {
    try {
      Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
    } catch (ExpiredJwtException ex) {
      throw new ForbiddenException(MessageConstant.EXPIRED_REFRESH_TOKEN);
    } catch (Exception ex) {
      throw new ForbiddenException(MessageConstant.INVALID_REFRESH_TOKEN);
    }
  }
}
