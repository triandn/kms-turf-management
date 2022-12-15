package com.kms.cntt.model;

import com.kms.cntt.enums.Role;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends AbstractEntity {
  @Id @GeneratedValue private UUID id;

  @Column(nullable = false, columnDefinition = "text")
  private String fullName;

  @Column(nullable = false)
  private String phoneNumber;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  private Role role;

  private String resetPasswordToken;
}
