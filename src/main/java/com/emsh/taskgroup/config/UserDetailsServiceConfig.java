package com.emsh.taskgroup.config;

import com.emsh.taskgroup.model.User;
import com.emsh.taskgroup.model.UserDetailsImpl;
import com.emsh.taskgroup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@Configuration
public class UserDetailsServiceConfig {
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Optional<User> user = userRepository.findByEmail(username);
            if (user.isPresent())
                return new UserDetailsImpl(user.get());
            else
                throw new UsernameNotFoundException("Usuario no encontrado");
        };
    }

}
