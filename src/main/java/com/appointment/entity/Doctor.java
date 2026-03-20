package com.appointment.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column (nullable = true)
    private String degree;
    @Column(nullable = false)
    private String specialization;
    @Column(nullable = false)
    private Boolean available = true;

    @Column(name = "user_id")
    private Long userId; // Link to the users table
}
