package com.kms.cntt.payload.dto;

import java.sql.Timestamp;

public interface NotificationDTO {
  String getId();

  String getSenderId();

  String getContent();

  String getReceiverId();

  Boolean getIsRead();

  Timestamp getCreatedAt();
}
