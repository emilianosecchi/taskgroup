package com.emsh.taskgroup.controller;


import com.emsh.taskgroup.dto.request.CreateNotificationRequest;
import com.emsh.taskgroup.dto.response.NotificationResponse;
import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.model.Notification;
import com.emsh.taskgroup.service.NotificationService;
import com.emsh.taskgroup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NotificationController(SimpMessagingTemplate messagingTemplate, NotificationService notificationService, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    public void sendNotificationToUser(Long userId, NotificationResponse notificationResponse) {
        String destination = "/topic/user/" + userId + "/notifications";
        messagingTemplate.convertAndSend(destination, notificationResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getAllNotificationsForUser(userId));
    }

    @PostMapping("/mark-as-read")
    public ResponseEntity<Object> markNotificationAsRead(@RequestParam(name = "notification_id") Long notificationId) throws CustomApiException {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/{userId}/mark-all-as-read")
    public ResponseEntity<Object> markAllAsRead(@PathVariable Long userId) throws CustomApiException {
        notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createNotification(@RequestBody CreateNotificationRequest request) throws CustomApiException {
        var notification = notificationService.createNotification(
                userService.findUserById(request.getUserId()),
                request.getMessage()
        );
        this.sendNotificationToUser(request.getUserId(), new NotificationResponse(notification));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<Object> deleteNotification(@RequestParam(name = "notification_id") Long notificationId) throws CustomApiException {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/{userId}/delete-all")
    public ResponseEntity<Object> deleteAllNotifications(@PathVariable Long userId) {
        notificationService.deleteAllNotificationsForUser(userId);
        return ResponseEntity.ok().build();
    }

}