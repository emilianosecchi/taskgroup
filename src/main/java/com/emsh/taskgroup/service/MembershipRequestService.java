package com.emsh.taskgroup.service;

import com.emsh.taskgroup.repository.MembershipRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MembershipRequestService {

    private final MembershipRequestRepository membershipRequestRepository;

    @Autowired
    public MembershipRequestService(MembershipRequestRepository membershipRequestRepository) {
        this.membershipRequestRepository = membershipRequestRepository;
    }
}
