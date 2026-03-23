package com.appointment.repository;
import com.appointment.entity.Rating;
import com.appointment.entity.Doctor;
import com.appointment.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating,Long>{
    Optional<Rating> findByPatientAndDoctor(Patient patient, Doctor doctor);
    boolean existsByPatientAndDoctor(Patient patient, Doctor doctor);
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.doctor = :doctor")
    Double findAverageRatingByDoctor(Doctor doctor);
}
