package com.kms.cntt.model;

import com.kms.cntt.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification extends AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column private UUID senderId;

  @Column private UUID objectableId;

  @Enumerated(EnumType.STRING)
  private NotificationType type;

  @Column(columnDefinition = "text")
  private String content;

  @Column private boolean isRead;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
