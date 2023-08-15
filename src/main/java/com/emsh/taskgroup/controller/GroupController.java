package com.emsh.taskgroup.controller;

import com.emsh.taskgroup.dto.request.CreateGroupRequest;
import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @RequestMapping("/create")
    public ResponseEntity<Object> createGroup(@Valid @RequestBody CreateGroupRequest request) throws CustomApiException {
        groupService.createGroup(request);
        return ResponseEntity.ok("El grupo se ha creado exitosamente.");
    }

    @RequestMapping("/delete")
    public ResponseEntity<Object> deleteGroup(@RequestParam(name = "user_id") Long userId, @RequestParam(name = "group_id") Long groupId) throws CustomApiException {
        groupService.deleteGroup(userId, groupId);
        return ResponseEntity.ok("El grupo se ha eliminado exitosamente.");
    }

}
