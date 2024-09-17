package com.emsh.taskgroup.event.listeners;

import com.emsh.taskgroup.event.events.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MembershipRequestListener {

    @EventListener
    public void onMembershipRequestAccepted(MembershipRequestAcceptedEvent event) {

    }

    @EventListener
    public void onMembershipRequestRejected(MembershipRequestRejectedEvent event) {

    }

}
