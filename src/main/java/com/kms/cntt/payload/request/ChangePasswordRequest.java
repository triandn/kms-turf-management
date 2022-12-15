package com.kms.cntt.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kms.cntt.common.Common;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChangePasswordRequest {
  @NotBlank private String oldPassword;

  @NotBlank
  @Pattern(regexp = Common.RULE_PASSWORD)
  private String newPassword;

  @NotBlank
  @Pattern(regexp = Common.RULE_PASSWORD)
  private String confirmNewPassword;

  public ChangePasswordRequest(String oldPassword, String newPassword, String confirmNewPassword) {
    this.oldPassword = oldPassword;
    this.newPassword = newPassword;
    this.confirmNewPassword = confirmNewPassword;
  }
}
