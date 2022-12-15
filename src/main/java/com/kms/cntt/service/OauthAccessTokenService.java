package com.kms.cntt.service;

import com.kms.cntt.model.OauthAccessToken;

import java.util.UUID;

public interface OauthAccessTokenService {
    OauthAccessToken createToken(UUID userId, UUID refreshToken);

    OauthAccessToken getOauthAccessTokenById(UUID id);

    OauthAccessToken getOauthAccessTokenByRefreshToken(UUID id);
    void revoke(OauthAccessToken oauthAccessToken);
}
