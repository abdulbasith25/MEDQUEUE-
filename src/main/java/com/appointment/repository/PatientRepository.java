package com.appointment.repository;
import com.appointment.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByPhone(String phone);
    Optional<Patient> findByPhone(String phone);
    Optional<Patient> findByUserId(Long userId);
}
