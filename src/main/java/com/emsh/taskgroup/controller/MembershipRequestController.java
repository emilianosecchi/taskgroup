package com.emsh.taskgroup.controller;

import com.emsh.taskgroup.service.MembershipRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/membership")
public class MembershipRequestController {

    private final MembershipRequestService membershipRequestService;

    @Autowired
    public MembershipRequestController(MembershipRequestService membershipRequestService) {
        this.membershipRequestService = membershipRequestService;
    }

    @RequestMapping
    public ResponseEntity<Object> createRequest() {
        // TODO
        return null;
    }

    @RequestMapping
    public ResponseEntity<Object> rejectRequest(@RequestParam(name = "admin_id") Long adminId, @RequestParam(name = "request_id") Long requestId) {
        // TODO
        return null;
    }

    @RequestMapping
    public ResponseEntity<Object> acceptRequest(@RequestParam(name = "admin_id") Long adminId, @RequestParam(name = "request_id") Long requestId) {
        // TODO
        return null;
    }

}