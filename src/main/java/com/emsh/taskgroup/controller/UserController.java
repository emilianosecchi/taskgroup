package com.emsh.taskgroup.controller;

import com.emsh.taskgroup.dto.request.AuthenticationRequest;
import com.emsh.taskgroup.dto.request.RegisterRequest;
import com.emsh.taskgroup.dto.response.AuthenticationResponse;
import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.service.GroupManagementService;
import com.emsh.taskgroup.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final GroupManagementService groupManagementService;

    @Autowired
    public UserController(UserService userService, GroupManagementService groupManagementService) {
        this.userService = userService;
        this.groupManagementService = groupManagementService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request) throws CustomApiException {
        userService.register(request);
        return ResponseEntity.ok("El usuario se ha creado exitosamente.");
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Object> authenticate(@Valid @RequestBody AuthenticationRequest request) throws CustomApiException {
        AuthenticationResponse authResponse = userService.authenticate(request);
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/{userId}/groups")
    public ResponseEntity<Object> getAllGroupsForUser(@PathVariable Long userId) throws CustomApiException {
        return ResponseEntity.ok(groupManagementService.getAllGroupsForUser(userId));
    }

}
