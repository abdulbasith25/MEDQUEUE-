package com.appointment.controller;

import com.appointment.dto.AuthRequest;
import com.appointment.dto.AuthResponse;
import com.appointment.dto.validation.OnLogin;
import com.appointment.dto.validation.OnRegister;
import com.appointment.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Validated(OnRegister.class) AuthRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(
            @RequestBody @Validated(OnLogin.class) AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
