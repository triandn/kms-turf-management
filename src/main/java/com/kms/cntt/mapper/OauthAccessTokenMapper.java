package com.kms.cntt.mapper;

import com.kms.cntt.config.SpringMapStructConfig;
import com.kms.cntt.model.OauthAccessToken;
import com.kms.cntt.payload.response.OauthAccessTokenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SpringMapStructConfig.class)
public interface OauthAccessTokenMapper {

  @Mapping(source = "accessToken", target = "accessToken")
  @Mapping(source = "refreshToken", target = "refreshToken")
  @Mapping(source = "expiresIn", target = "expiresIn")
  OauthAccessTokenResponse toOauthAccessTokenResponse(
      OauthAccessToken oauthAccessToken, String accessToken, String refreshToken, long expiresIn);
}
