package com.emsh.taskgroup.repository;

import com.emsh.taskgroup.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Consultar las notificaciones no leídas de un usuario
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
}