package com.kms.cntt.controller;

import com.kms.cntt.common.Common;
import com.kms.cntt.payload.general.ResponseDataAPI;
import com.kms.cntt.payload.request.NotificationRequest;
import com.kms.cntt.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotificationController {

  private final ConnectionFactory connectionFactory;
  private final NotificationService notificationService;

  @GetMapping("/notifications/endpoint/{id}")
  public ResponseEntity<ResponseDataAPI> getEndpoint(@PathVariable UUID id) {
    RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);

    String queueName = Common.generateQueueName(id);
    rabbitAdmin.declareQueue(new Queue(queueName, false, false, false));

    String exchangeName = Common.EXCHANGE_NAME_PREFIX + id;
    rabbitAdmin.declareExchange(new DirectExchange(exchangeName, false, false));
    rabbitAdmin.declareBinding(
        new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, id.toString(), null));
    return ResponseEntity.ok(
        ResponseDataAPI.builder().status(Common.SUCCESS).data(queueName).build());
  }

  @PostMapping("/notifications")
  public ResponseEntity<ResponseDataAPI> pushNotification(
      @RequestBody NotificationRequest notificationRequest) {
    return ResponseEntity.ok(
        ResponseDataAPI.successWithoutMeta(
            notificationService.createNotification(notificationRequest)));
  }

  @GetMapping("/notifications/{id}")
  public ResponseEntity<ResponseDataAPI> getNotificationByUserId(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "paging", defaultValue = "10") int paging,
      @PathVariable UUID id) {
    Pageable pageable = PageRequest.of(page - 1, paging);
    return ResponseEntity.ok(notificationService.findAllNotificationByUserId(pageable, id));
  }

  @GetMapping("/notifications/unreadNumber/{id}")
  public ResponseEntity<ResponseDataAPI> countUnreadNotification(@PathVariable UUID id) {
    return ResponseEntity.ok(notificationService.countUnreadNotification(id));
  }
}
