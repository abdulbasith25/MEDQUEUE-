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
    @Column(nullable = true)
    private Long totalDurationTook = 0L;
    @Column(nullable = true)
    private Long totalConsultations = 0L;

    @Column(name = "user_id")
    private Long userId; 
}
