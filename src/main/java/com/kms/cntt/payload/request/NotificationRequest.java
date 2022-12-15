package com.kms.cntt.payload.request;

import com.kms.cntt.enums.NotificationType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
public class NotificationRequest {

  @NotBlank private UUID senderId;

  @NotBlank private UUID objectableId;

  @NotBlank private NotificationType notificationType;

  @NotBlank private String content;

  @NotBlank private UUID receiverId;
}
