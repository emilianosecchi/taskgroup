package com.emsh.taskgroup.controller;

import com.emsh.taskgroup.dto.request.RegisterRequest;
import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.ok("Usuario creado");
        } catch (CustomApiException e) {
            return new ResponseEntity<>(e, e.getHttpStatus());
        }
    }

}
