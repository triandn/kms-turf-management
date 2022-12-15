package com.kms.cntt.payload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kms.cntt.enums.TurfType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TurfResponse {
  private UUID id;

  public TurfResponse() {
  }

  private UUID locationTurfId;

  private Timestamp createdAt;

  private String name;

  private BigDecimal hourlyFee;

  private float rating;

  private TurfType type;

  private String imageLink;
}
