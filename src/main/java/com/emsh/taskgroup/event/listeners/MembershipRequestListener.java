package com.emsh.taskgroup.event.listeners;

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

    @EventListener
    public void onMembershipRequestCompletedEvent(MembershipRequestCompletedEvent event) {
        // Lógica para notificaciones en tiempo real
        // ...
        var message = (event.getRequestStatus().equals(MembershipRequestStatus.REJECTED)) ?
                "La solicitud para ingresar al grupo: " + event.getGroup().getName() + " ha sido rechazada. Por favor, intente generando otra solicitud."
                :
                "La solicitud para ingresar al grupo: " + event.getGroup().getName() + " ha sido aceptada. Ya podes ver las tareas de otros miembros y crear nuevas.";
        notificationService.createNotification(event.getUser(), message);
    }
}
