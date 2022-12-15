package com.kms.cntt.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LocationTurfRequest {
  @NotBlank
  @Size(max = 200)
  private String name;

  @NotBlank
  @Size(max = 200)
  private String address;

  @NotBlank private String imageLink;
}
