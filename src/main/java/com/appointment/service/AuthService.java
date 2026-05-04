package com.appointment.service;

import com.appointment.config.JwtUtils;
import com.appointment.dto.AuthRequest;
import com.appointment.dto.AuthResponse;
import com.appointment.entity.Doctor;
import com.appointment.entity.Patient;
import com.appointment.entity.Role;
import com.appointment.entity.User;
import com.appointment.repository.DoctorRepository;
import com.appointment.repository.PatientRepository;
import com.appointment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.userdetails.UsernameNotFoundException;



@Service
@RequiredArgsConstructor
public class AuthService { 

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;

    public AuthResponse register(AuthRequest request) {
        Role selectedRole = request.getRole() != null ? request.getRole() : Role.ROLE_PATIENT;
        // var user = User.builder()
        //         .username(request.getUsername())
        //         .password(passwordEncoder.encode(request.getPassword()))
        //         .role(selectedRole)
        //         .build();

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(selectedRole);

        User savedUser = userRepository.save(user);

        if (selectedRole == Role.ROLE_DOCTOR) {
            Doctor doctor = new Doctor();
            doctor.setName(request.getName());
            doctor.setSpecialization(request.getSpecialization());
            doctor.setDegree(request.getDegree());
            doctor.setUserId(savedUser.getId());
            doctorRepository.save(doctor);
        } else if (selectedRole == Role.ROLE_PATIENT) {
            Patient patient = new Patient();
            patient.setName(request.getName());
            patient.setPhone(request.getPhone());
            patient.setEmail(request.getEmail());
            patient.setUserId(savedUser.getId());
            patientRepository.save(patient);
        }

        var jwtToken = jwtUtils.generateToken(savedUser);
        AuthResponse response = new AuthResponse();
        response.setToken(jwtToken);
        response.setUsername(savedUser.getUsername());
        response.setRole(savedUser.getRole().name());
        return response;
    }

    public AuthResponse authenticate(AuthRequest request) {

        String key = request.getUsername(); 

    if (loginAttemptService.isBlocked(key)) {
        throw new RuntimeException("Too many login attempts. Try again later.");
    }

    try {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // ✅ success → reset attempts
        loginAttemptService.loginSucceeded(key);

    } catch (Exception e) {
        // ❌ failure → increment attempts
        loginAttemptService.loginFailed(key);
        throw e;
    }
       var user = userRepository.findByUsername(request.getUsername())
            .orElseThrow();

    var jwtToken = jwtUtils.generateToken(user);
        AuthResponse response = new AuthResponse();
        response.setToken(jwtToken);
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());
        return response;
    }

    @CacheEvict(value = "userDetails", key = "#username")
    public void changePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        // The cache entry for this 'username' is now deleted from Caffeine
    }
}
