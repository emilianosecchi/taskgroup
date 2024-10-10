package com.emsh.taskgroup.controller;

import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.service.GroupManagementService;
import com.emsh.taskgroup.service.MembershipRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/membership")
public class MembershipRequestController {

    private final MembershipRequestService membershipRequestService;
    private final GroupManagementService groupManagementService;

    @Autowired
    public MembershipRequestController(MembershipRequestService membershipRequestService, GroupManagementService groupManagementService) {
        this.membershipRequestService = membershipRequestService;
        this.groupManagementService = groupManagementService;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createRequest(@RequestParam(name = "group") String groupIdHash, @RequestParam(name = "user_id") Long userId) throws CustomApiException {
        groupManagementService.createMembershipRequest(
                userId,
                groupManagementService.decryptGroupId(groupIdHash)
        );
        return ResponseEntity.ok("La solicitud se ha generado exitosamente.");
    }

    @PostMapping("/reject")
    public ResponseEntity<Object> rejectRequest(@RequestParam(name = "admin_id") Long adminId, @RequestParam(name = "request_id") Long requestId) throws CustomApiException  {
        membershipRequestService.rejectRequest(adminId, requestId);
        return ResponseEntity.ok("La solicitud se ha rechazado exitosamente.");
    }

    @PostMapping("/accept")
    public ResponseEntity<Object> acceptRequest(@RequestParam(name = "admin_id") Long adminId, @RequestParam(name = "request_id") Long requestId) throws CustomApiException {
        groupManagementService.acceptMembershipRequest(adminId, requestId);
        return ResponseEntity.ok("La solicitud se ha aceptado exitosamente.");
    }

    @GetMapping("/pending-requests")
    public ResponseEntity<Object> getPendingRequests(@RequestParam(name = "group_id") String groupIdHash) throws CustomApiException {
        return ResponseEntity.ok(membershipRequestService.findAllPendingRequestForGroup(
                groupManagementService.decryptGroupId(groupIdHash))
        );
    }

}