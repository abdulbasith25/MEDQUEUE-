package com.appointment.config;

import com.appointment.entity.Role;
import com.appointment.entity.User;
import com.appointment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    @Value("${admin.username}")
    private String username;
    @Value("${admin.password}")
    private String password;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User admin = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
            // System.out.println("Default Admin account created: " + username + " / " + password);
        }
    }
}
