package com.kms.cntt.service;

import com.kms.cntt.enums.NotificationType;
import com.kms.cntt.model.Notification;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.NotificationRequest;
import com.kms.cntt.payload.response.NotificationResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {

  NotificationResponse createNotification(NotificationRequest notificationRequest);

  void createNotificationMain(
      UUID senderId,
      UUID objectableId,
      NotificationType notificationType,
      String content,
      UUID receiverId,
      boolean isSave);

  ResponseDataAPI findAllNotificationByUserId(Pageable pageable, UUID userId);

  ResponseDataAPI countUnreadNotification(UUID userId);

  Notification save(
      UUID senderId,
      UUID objectableId,
      NotificationType notificationType,
      String content,
      UUID receiverId,
      boolean isSave);
}
