package com.kms.cntt.background;

import com.kms.cntt.model.Notification;

import java.util.UUID;

public interface PushNotification {

  void pushNotification(String exchangePrefix, Notification notification, UUID receiverId);
}
