package com.emsh.taskgroup.controller;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @MessageMapping("/notifications")
    @SendTo("/topic/notifications")
    public void broadcastNotifications() {
    }

}
