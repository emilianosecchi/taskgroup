package com.emsh.taskgroup.event.events;

import com.emsh.taskgroup.model.Group;
import com.emsh.taskgroup.model.MembershipRequestStatus;
import com.emsh.taskgroup.model.User;
import org.springframework.context.ApplicationEvent;

public class MembershipRequestCompletedEvent extends ApplicationEvent {
    private Group group;
    private User user;
    private MembershipRequestStatus requestStatus;
    public MembershipRequestCompletedEvent(Object source, Group group, User user, MembershipRequestStatus status) {
        super(source);
        this.group = group;
        this.user = user;
        this.requestStatus = status;
    }

    public Group getGroup() {
        return group;
    }

    public User getUser() {
        return user;
    }

    public MembershipRequestStatus getRequestStatus() {
        return requestStatus;
    }

}