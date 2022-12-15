package com.kms.cntt.payload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kms.cntt.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponse {

  private UUID id;

  private Timestamp createdAt;

  private String fullName;

  public UserResponse() {

  }

  private String phoneNumber;

  private String username;

  @Enumerated(EnumType.STRING)
  private Role role;
}
