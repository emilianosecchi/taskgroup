package com.emsh.taskgroup.event.events;

import org.springframework.context.ApplicationEvent;

public class MembershipRequestRejectedEvent extends ApplicationEvent {
    private String message;
    public MembershipRequestRejectedEvent(Object source) {
        super(source);
    }
    public String getMessage() {
        return message;
    }
}