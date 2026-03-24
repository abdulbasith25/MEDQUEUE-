package com.appointment.repository;
import com.appointment.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long userId);
    Page<Doctor> findByNameContainingIgnoreCaseAndSpecializationContainingIgnoreCase(
            String name,
            String specialization,
            Pageable pageable
    );
}
