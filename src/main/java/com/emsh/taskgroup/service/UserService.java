package com.emsh.taskgroup.service;

import com.emsh.taskgroup.config.JwtService;
import com.emsh.taskgroup.dto.request.RegisterRequest;
import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.model.Role;
import com.emsh.taskgroup.model.User;
import com.emsh.taskgroup.model.UserDetailsImpl;
import com.emsh.taskgroup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest request) throws CustomApiException {
        if (userRepository.findByEmail(request.getEmail()).isEmpty()) {
            User user = User.builder()
                    .email(request.getEmail())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
            String jwtToken = jwtService.generateToken(new UserDetailsImpl(user));
        } else
            throw new CustomApiException("El email ya se encuentra registrado", HttpStatus.CONFLICT);
    }

}