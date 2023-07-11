package com.emsh.taskgroup.service;

import com.emsh.taskgroup.config.JwtService;
import com.emsh.taskgroup.dto.request.AuthenticationRequest;
import com.emsh.taskgroup.dto.request.RegisterRequest;
import com.emsh.taskgroup.dto.response.AuthenticationResponse;
import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.model.Role;
import com.emsh.taskgroup.model.User;
import com.emsh.taskgroup.model.UserDetailsImpl;
import com.emsh.taskgroup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
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
        } else
            throw new CustomApiException("El email ya se encuentra registrado", HttpStatus.CONFLICT);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws CustomApiException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            Optional<User> user = userRepository.findByEmail(request.getEmail());
            if (user.isPresent()) {
                String jwtToken = jwtService.generateToken(
                        new UserDetailsImpl(user.get())
                );
                return new AuthenticationResponse(jwtToken);
            } else
                throw new CustomApiException("No fue posible iniciar la sesión del usuario", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException e) {
            throw new CustomApiException("El email y/o contraseña no son válidos", HttpStatus.UNAUTHORIZED);
        }
    }

}