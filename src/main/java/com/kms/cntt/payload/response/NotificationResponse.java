package com.kms.cntt.payload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kms.cntt.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NotificationResponse {
  private UUID id;

  private UUID senderId;

  private UUID objectableId;

  private String content;

  private UUID receiverId;

  private boolean isRead;

  private Timestamp createdAt;

  public static NotificationResponse createNotificationResponseWithFullInfo(
      Notification notification) {
    return new NotificationResponse(
        notification.getId(),
        notification.getSenderId(),
        notification.getObjectableId(),
        notification.getContent(),
        notification.getUser().getId(),
        notification.isRead(),
        notification.getCreatedAt());
  }
}
