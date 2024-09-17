package com.emsh.taskgroup.event.events;

import org.springframework.context.ApplicationEvent;

public class MembershipRequestAcceptedEvent extends ApplicationEvent {
    private String message;
    public MembershipRequestAcceptedEvent(Object source, String message) {
        super(source);
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
