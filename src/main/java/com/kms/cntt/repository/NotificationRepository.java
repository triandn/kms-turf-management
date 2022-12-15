package com.kms.cntt.repository;

import com.kms.cntt.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
  Page<Notification> findAllByUserId(Pageable pageable, UUID userId);

  @Query(
      value =
          "select "
              + "    count(*) "
              + "from "
              + "    \"JNotifications\" j "
              + "where "
              + "    j.\"isRead\" = false",
      nativeQuery = true)
  long countNotificationUnreadByUserId(UUID userId);
}
