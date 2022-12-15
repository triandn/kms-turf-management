package com.kms.cntt.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ScheduleRequest {
  @NotNull private UUID turfId;

  @NotBlank
  @Size(max = 200)
  private String title;

  @NotBlank
  @Size(max = 200)
  private String description;

  private boolean requireReferee;

  @NotNull private Date startTime;

  @NotNull private Date endTime;

  public ScheduleRequest(UUID turfId, String title, String description, Date startTime, Date endTime) {
    this.turfId = turfId;
    this.title = title;
    this.description = description;
    this.startTime = startTime;
    this.endTime = endTime;
  }
  public ScheduleRequest(){

  }
}
