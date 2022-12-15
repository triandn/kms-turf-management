package com.kms.cntt.service.impl;

import com.kms.cntt.background.PushNotification;
import com.kms.cntt.common.Common;
import com.kms.cntt.common.CommonFunction;
import com.kms.cntt.enums.NotificationType;
import com.kms.cntt.model.Notification;
import com.kms.cntt.payload.general.PageInfo;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.NotificationRequest;
import com.kms.cntt.payload.response.NotificationResponse;
import com.kms.cntt.payload.response.UnreadNumberNotificationResponses;
import com.kms.cntt.repository.NotificationRepository;
import com.kms.cntt.repository.UserRepository;
import com.kms.cntt.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final PushNotification pushNotification;

  private final UserRepository userRepository;

  @Override
  @Async("asyncExecutor")
  public NotificationResponse createNotification(NotificationRequest notificationRequest) {
    Notification notification =
        this.save(
            notificationRequest.getSenderId(),
            notificationRequest.getObjectableId(),
            notificationRequest.getNotificationType(),
            notificationRequest.getContent(),
            notificationRequest.getReceiverId(),
            true);
    NotificationResponse notificationResponse =
        NotificationResponse.createNotificationResponseWithFullInfo(notification);
    pushNotification.pushNotification(
        Common.EXCHANGE_NAME_PREFIX, notification, notificationRequest.getReceiverId());
    return notificationResponse;
  }

  @Override
  public void createNotificationMain(
      UUID senderId,
      UUID objectableId,
      NotificationType notificationType,
      String content,
      UUID receiverId,
      boolean isSave) {
    Notification notification =
        this.save(senderId, objectableId, notificationType, content, receiverId, isSave);
    pushNotification.pushNotification(Common.EXCHANGE_NAME_PREFIX, notification, receiverId);
  }

  @Override
  public ResponseDataAPI findAllNotificationByUserId(Pageable pageable, UUID userId) {
    Page<Notification> pages = notificationRepository.findAllByUserId(pageable, userId);
    PageInfo pageInfo =
        new PageInfo(pages.getNumber() + 1, pages.getTotalPages(), pages.getTotalElements());
    return ResponseDataAPI.success(pages.getContent(), pageInfo);
  }

  @Override
  public ResponseDataAPI countUnreadNotification(UUID userId) {
    UnreadNumberNotificationResponses unreadNumberNotificationResponses =
        new UnreadNumberNotificationResponses();
    unreadNumberNotificationResponses.setAmount(
        notificationRepository.countNotificationUnreadByUserId(userId));
    return ResponseDataAPI.successWithoutMeta(unreadNumberNotificationResponses);
  }

  @Override
  @Transactional
  public Notification save(
      UUID senderId,
      UUID objectableId,
      NotificationType notificationType,
      String content,
      UUID receiverId,
      boolean isSave) {
    Notification notification = new Notification();
    notification.setCreatedAt(CommonFunction.getCurrentDateTime());
    notification.setSenderId(senderId);
    notification.setObjectableId(objectableId);
    notification.setType(notificationType);
    notification.setContent(content);
    notification.setUser(userRepository.findById(receiverId).get());
    notification.setRead(false);

    if (isSave) {
      return notificationRepository.save(notification);
    } else {
      notification.setCreatedAt(Common.getCurrentDateTime());
      return notification;
    }
  }
}
