package com.kms.cntt.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kms.cntt.common.Common;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TurfRequest {
  @NotBlank
  @Size(max = 200)
  private String name;

  @NotNull private UUID locationTurfId;

  @NotNull private BigDecimal hourlyFee;

  @NotBlank
  @Pattern(regexp = Common.RULE_TURF_TYPE)
  private String type;

  @NotBlank private String imageLink;
}
