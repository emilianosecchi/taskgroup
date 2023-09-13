package com.emsh.taskgroup.controller;

import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.service.MembershipRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @RequestMapping("/create")
    public ResponseEntity<Object> createRequest(@RequestParam(name = "user_id") Long userId, @RequestParam(name = "group") String encryptedGroupId) throws CustomApiException {
        membershipRequestService.createRequest(userId, encryptedGroupId);
        return ResponseEntity.ok("La solicitud se ha generado exitosamente.");
    }

    @RequestMapping("/reject")
    public ResponseEntity<Object> rejectRequest(@RequestParam(name = "admin_id") Long adminId, @RequestParam(name = "request_id") Long requestId) throws CustomApiException  {
        membershipRequestService.rejectRequest(adminId, requestId);
        return ResponseEntity.ok("La solicitud se ha rechazado exitosamente.");
    }

    @RequestMapping("/accept")
    public ResponseEntity<Object> acceptRequest(@RequestParam(name = "admin_id") Long adminId, @RequestParam(name = "request_id") Long requestId) throws CustomApiException {
        membershipRequestService.acceptRequest(adminId, requestId);
        return ResponseEntity.ok("La solicitud se ha aceptado exitosamente.");
    }

    @GetMapping("/pending-requests")
    public ResponseEntity<Object> getPendingRequests(@RequestParam(name = "group_id") Long groupId) {
        return ResponseEntity.ok(membershipRequestService.findAllPendingRequestForGroup(groupId));
    }

}