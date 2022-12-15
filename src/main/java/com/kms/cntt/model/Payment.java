package com.kms.cntt.model;

import com.kms.cntt.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment extends AbstractEntity {
  @Id @GeneratedValue private UUID id;

  private String vnpOrderInfo;

  private String orderType;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(nullable = false)
  private String locate;

  @Column(nullable = false)
  private String ipAddress;

  @Column(columnDefinition = "text")
  private String paymentUrl;

  @Column()
  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  @Column(nullable = false)
  private String txnRef;

  @Column(nullable = false)
  private Date timeOver;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "schedule_id")
  private Schedule schedule;
}
