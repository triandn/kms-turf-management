package com.kms.cntt.model;

import com.kms.cntt.enums.ScheduleStatus;
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
@Table(name = "schedules")
public class Schedule extends AbstractEntity {
  @Id @GeneratedValue private UUID id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private Date startTime;

  @Column(nullable = false)
  private Date endTime;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private BigDecimal price;

  @Column private boolean requireReferee;

  private String reasonCancel;

  private Boolean isPayment;

  @Enumerated(EnumType.STRING)
  private ScheduleStatus status;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "turf_id")
  private Turf turf;

  @OneToOne
  @JoinColumn(name = "referee_id")
  private User referee;
}
