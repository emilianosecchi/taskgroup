package com.emsh.taskgroup.controller;

import com.emsh.taskgroup.dto.request.CreateGroupRequest;
import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.service.GroupManagementService;
import com.emsh.taskgroup.service.GroupService;
import com.emsh.taskgroup.service.UserGroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group")
public class GroupController {

    private final GroupManagementService groupManagementService;
    private final GroupService groupService;
    private final UserGroupService userGroupService;

    @Autowired
    public GroupController(GroupManagementService groupManagementService, GroupService groupService, UserGroupService userGroupService) {
        this.groupManagementService = groupManagementService;
        this.groupService = groupService;
        this.userGroupService = userGroupService;
    }

    @RequestMapping("/create")
    public ResponseEntity<Object> createGroup(@Valid @RequestBody CreateGroupRequest request) throws CustomApiException {
        return ResponseEntity.ok(groupManagementService.createGroup(request));
    }

    @RequestMapping("/delete")
    public ResponseEntity<Object> deleteGroup(@RequestParam(name = "user_id") Long userId, @RequestParam(name = "group_id") String groupIdHash) throws CustomApiException {
        groupManagementService.deleteGroup(
                userId,
                groupManagementService.decryptGroupId(groupIdHash)
        );
        return ResponseEntity.ok("El grupo se ha eliminado exitosamente.");
    }

    @RequestMapping("/leave")
    public ResponseEntity<Object> leaveGroup(@RequestParam(name = "user_id") Long userId, @RequestParam(name = "group_id") String groupIdHash) throws CustomApiException {
        userGroupService.removeParticipantFromGroup(
                groupManagementService.decryptGroupId(groupIdHash),
                userId
        );
        return ResponseEntity.ok("El usuario ha sido removido del grupo exitosamente.");
    }

    @GetMapping("/get-all-groups")
    public ResponseEntity<Object> getAllGroupsForUser(@RequestParam(name = "user_id") Long userId) throws CustomApiException {
        return ResponseEntity.ok(groupManagementService.getAllGroupsForUser(userId));
    }

    @GetMapping("/join")
    public ResponseEntity<Object> joinGroup(@RequestParam(name = "group_id") String groupIdHash, @RequestParam(name = "user_id") Long userId) throws CustomApiException {
        return ResponseEntity.ok(groupService.joinGroup(
                groupManagementService.decryptGroupId(groupIdHash),
                userId)
        );
    }

}
