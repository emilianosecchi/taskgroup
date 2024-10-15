package com.emsh.taskgroup.event.listeners;

import com.emsh.taskgroup.controller.NotificationController;
import com.emsh.taskgroup.dto.response.NotificationResponse;
import com.emsh.taskgroup.event.events.TaskCompletedEvent;
import com.emsh.taskgroup.event.events.TaskCreatedEvent;
import com.emsh.taskgroup.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TaskListener {

    private final NotificationService notificationService;
    private final NotificationController notificationController;

    @Autowired
    public TaskListener(NotificationService notificationService, NotificationController notificationController) {
        this.notificationService = notificationService;
        this.notificationController = notificationController;
    }

    @EventListener
    public void onTaskCompletedEvent(TaskCompletedEvent event) {
        event.getTask().getGroup().getAllParticipants().forEach(participant -> {
            var notification = notificationService.createNotification(participant, "El usuario " + event.getTask().getFinisher().getFullName() + " ha completado una tarea en el grupo " + event.getTask().getGroup().getName());
            notificationController.sendNotificationToUser(participant.getId(), new NotificationResponse(notification));
        });
    }

    @EventListener
    public void onTaskCreatedEvent(TaskCreatedEvent event) {
        event.getTask().getGroup().getAllParticipants().forEach(participant -> {
            var notification = notificationService.createNotification(participant, "El usuario " + event.getTask().getCreator().getFullName() + " ha creado una nueva tarea en el grupo " + event.getTask().getGroup().getName());
            notificationController.sendNotificationToUser(participant.getId(), new NotificationResponse(notification));
        });
    }

}