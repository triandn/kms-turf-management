package com.kms.cntt.background.impl;

import com.kms.cntt.background.PushNotification;
import com.kms.cntt.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationImpl implements PushNotification {
  private final RabbitTemplate rabbitTemplate;

  @Override
  @Async("asyncExecutor")
  public void pushNotification(String exchangePrefix, Notification notification, UUID receiverId) {
    try {
      if (this.checkExchangeExist(exchangePrefix + receiverId)) {
        rabbitTemplate.convertAndSend(
            exchangePrefix + receiverId, receiverId.toString(), notification);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  private boolean checkExchangeExist(String exchange) {
    try {
      rabbitTemplate.execute(channel -> channel.exchangeDeclarePassive(exchange));
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
