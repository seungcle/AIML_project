package com.group.receiptapp.repository.notification;

import com.group.receiptapp.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberIdAndIsReadFalse(Long memberId);

}
