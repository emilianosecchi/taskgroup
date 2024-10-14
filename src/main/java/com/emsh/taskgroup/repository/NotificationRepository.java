package com.emsh.taskgroup.repository;

import com.emsh.taskgroup.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Modifying
    @Query("update Notification n set n.isRead = true where n.user.id = ?1 and n.isRead = false")
    void markAllNotificationsAsRead(Long userId);

    @Modifying
    @Query("delete from Notification n where n.user.id = ?1")
    void deleteAllNotifications(Long userId);

    Optional<List<Notification>> findByUserIdOrderByCreationTimestampDesc(Long userId);

}