package com.appointment.dto;

import com.appointment.dto.validation.OnLogin;
import com.appointment.dto.validation.OnRegister;
import com.appointment.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    // Required for BOTH login and register
    @NotBlank(groups = {OnLogin.class, OnRegister.class}, message = "Username is required")
    private String username;

    @NotBlank(groups = {OnLogin.class, OnRegister.class}, message = "Password is required")
    private String password;

    // Required only during REGISTER
    @NotBlank(groups = OnRegister.class, message = "Name is required for registration")
    private String name;

    @NotNull(groups = OnRegister.class, message = "Role is required for registration")
    private Role role;

    // Optional fields - no validation enforced
    private String phone;
    private String email;

    // Doctor-specific optional fields
    private String specialization;
    private String degree;
}
