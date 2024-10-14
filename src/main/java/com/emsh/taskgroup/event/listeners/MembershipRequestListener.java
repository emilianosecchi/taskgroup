package com.emsh.taskgroup.event.listeners;

import com.emsh.taskgroup.controller.NotificationController;
import com.emsh.taskgroup.dto.response.NotificationResponse;
import com.emsh.taskgroup.event.events.*;
import com.emsh.taskgroup.model.MembershipRequestStatus;
import com.emsh.taskgroup.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MembershipRequestListener {

    private final NotificationService notificationService;
    private final NotificationController notificationController;

    @EventListener
    public void onMembershipRequestCompletedEvent(MembershipRequestCompletedEvent event) {
        var message = (event.getRequestStatus().equals(MembershipRequestStatus.REJECTED)) ?
                "La solicitud para ingresar al grupo: " + event.getGroup().getName() + " ha sido rechazada. Por favor, intente generando otra solicitud."
                :
                "La solicitud para ingresar al grupo: " + event.getGroup().getName() + " ha sido aceptada. Ya podes ver las tareas de otros miembros y crear nuevas.";
        var notification = notificationService.createNotification(event.getUser(), message);
        notificationController.sendNotificationToUser(event.getUser().getId(), new NotificationResponse(notification));
    }

    @EventListener
    public void onMembershipRequestCreatedEvent(MembershipRequestCreatedEvent event) {
        var message = "El usuario con nombre: " + event.getRequester().getFirstName() + " " + event.getRequester().getLastName() + " ha solicitado unirse al grupo: " + event.getGroup().getName();
        event.getGroup().getAdmins().forEach(
                admin -> {
                    var notification = notificationService.createNotification(admin, message);
                    notificationController.sendNotificationToUser(admin.getId(), new NotificationResponse(notification));
                }
        );
    }
}
