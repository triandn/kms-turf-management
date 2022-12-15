package com.kms.cntt.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kms.cntt.enums.ScheduleStatus;
import com.kms.cntt.model.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleResponse {
  private UUID id;

  private UserResponse userResponse;

  private Timestamp createdAt;

  private String title;

  private String description;

  private String reasonCancel;

  private ScheduleStatus status;

  private Date startTime;

  private Date endTime;

  private TurfResponse turfResponse;

  private LocationTurfResponse locationTurfResponse;

  private boolean requireReferee;

  private UserResponse refereeResponse;

  private BigDecimal price;

  private boolean isPayment;

  public static ScheduleResponse createScheduleResponseWithMainInfo(Schedule schedule) {
    return ScheduleResponse.builder()
        .id(schedule.getId())
        .createdAt(schedule.getCreatedAt())
        .title(schedule.getTitle())
        .description(schedule.getDescription())
        .reasonCancel(schedule.getReasonCancel())
        .status(schedule.getStatus())
        .startTime(schedule.getStartTime())
        .endTime(schedule.getEndTime())
        .requireReferee(schedule.isRequireReferee())
        .price(schedule.getPrice())
        .isPayment(schedule.getIsPayment())
        .build();
  }

  public static ScheduleResponse createScheduleResponseWithFullInfo(
      Schedule schedule, TurfResponse turfResponse, LocationTurfResponse locationTurfResponse) {
    return ScheduleResponse.builder()
        .id(schedule.getId())
        .createdAt(schedule.getCreatedAt())
        .title(schedule.getTitle())
        .description(schedule.getDescription())
        .reasonCancel(schedule.getReasonCancel())
        .status(schedule.getStatus())
        .startTime(schedule.getStartTime())
        .endTime(schedule.getEndTime())
        .turfResponse(turfResponse)
        .locationTurfResponse(locationTurfResponse)
        .requireReferee(schedule.isRequireReferee())
        .price(schedule.getPrice())
        .isPayment(schedule.getIsPayment())
        .build();
  }

  public static ScheduleResponse createScheduleResponseWithFullInfo(
      Schedule schedule, TurfResponse turfResponse) {
    return ScheduleResponse.builder()
        .id(schedule.getId())
        .createdAt(schedule.getCreatedAt())
        .title(schedule.getTitle())
        .description(schedule.getDescription())
        .reasonCancel(schedule.getReasonCancel())
        .status(schedule.getStatus())
        .startTime(schedule.getStartTime())
        .endTime(schedule.getEndTime())
        .turfResponse(turfResponse)
        .requireReferee(schedule.isRequireReferee())
        .price(schedule.getPrice())
        .isPayment(schedule.getIsPayment())
        .build();
  }

  public static ScheduleResponse createScheduleResponseWithFullInfoAdmin(
      Schedule schedule,
      UserResponse userResponse,
      TurfResponse turfResponse,
      LocationTurfResponse locationTurfResponse,
      UserResponse refereeResponse) {
    return ScheduleResponse.builder()
        .id(schedule.getId())
        .userResponse(userResponse)
        .createdAt(schedule.getCreatedAt())
        .title(schedule.getTitle())
        .description(schedule.getDescription())
        .reasonCancel(schedule.getReasonCancel())
        .status(schedule.getStatus())
        .startTime(schedule.getStartTime())
        .endTime(schedule.getEndTime())
        .turfResponse(turfResponse)
        .locationTurfResponse(locationTurfResponse)
        .requireReferee(schedule.isRequireReferee())
        .refereeResponse(refereeResponse)
        .price(schedule.getPrice())
        .isPayment(schedule.getIsPayment())
        .build();
  }
}
