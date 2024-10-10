package com.emsh.taskgroup.event.events;

import com.emsh.taskgroup.model.Group;
import com.emsh.taskgroup.model.MembershipRequestStatus;
import com.emsh.taskgroup.model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MembershipRequestCompletedEvent extends ApplicationEvent {
    private final Group group;
    private final User user;
    private final MembershipRequestStatus requestStatus;
    public MembershipRequestCompletedEvent(Object source, Group group, User user, MembershipRequestStatus status) {
        super(source);
        this.group = group;
        this.user = user;
        this.requestStatus = status;
    }

}