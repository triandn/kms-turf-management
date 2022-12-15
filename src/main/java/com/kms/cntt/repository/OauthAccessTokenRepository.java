package com.kms.cntt.repository;

import com.kms.cntt.model.OauthAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OauthAccessTokenRepository extends JpaRepository<OauthAccessToken, UUID> {
  Optional<OauthAccessToken> findByRefreshToken(UUID refreshToken);
}
