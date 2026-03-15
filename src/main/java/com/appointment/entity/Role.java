package com.appointment.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum Role {
    ROLE_PATIENT,
    ROLE_ADMIN,
    ROLE_DOCTOR
}
