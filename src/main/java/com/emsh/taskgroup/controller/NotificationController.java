package com.emsh.taskgroup.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class NotificationController {
    @GetMapping(path = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter notifications() {
        SseEmitter emitter = new SseEmitter();
        return emitter;
    }
}
