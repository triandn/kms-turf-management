package com.kms.cntt.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kms.cntt.common.Common;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.security.cert.CertPathBuilder;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserSignUpRequest {

  @NotBlank private String username;

  @NotBlank
  @Pattern(regexp = Common.RULE_PASSWORD)
  private String password;

  @NotBlank private String passwordConfirmation;

  @NotBlank
  @Pattern(regexp = Common.RULE_ROLE)
  private String role;

  @NotBlank private String fullName;

  @NotBlank private String phoneNumber;

}
