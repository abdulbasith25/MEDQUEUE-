package com.appointment.repository;
import com.appointment.entity.Appointment;
import com.appointment.entity.AppointmentStatus;
import com.appointment.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT MAX(a.tokenNumber) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.date = :date")
    Optional<Integer> findMaxTokenNumberByDoctorAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);
    List<Appointment> findByDoctorAndDateOrderByTokenNumberAsc(Doctor doctor, LocalDate date);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.date = :date AND a.status = :status ORDER BY a.tokenNumber ASC")
    List<Appointment> findNextBookedAppointment(@Param("doctorId") Long doctorId, @Param("date") LocalDate date, @Param("status") AppointmentStatus status);
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.date = :date AND a.status = 'DONE' ORDER BY a.tokenNumber DESC")
    List<Appointment> findLastDoneAppointment(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);
    List<Appointment> findByDateOrderByDoctorIdAscTokenNumberAsc(LocalDate date);
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.date = :date AND a.status = :status")
    long countByDoctorAndDateAndStatus(@Param("doctorId") Long doctorId, @Param("date") LocalDate date, @Param("status") AppointmentStatus status);
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.date = :date AND a.status = :status")
    long countByDateAndStatus(@Param("date") LocalDate date, @Param("status") AppointmentStatus status);
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.date = :date")
    long countByDate(@Param("date") LocalDate date);
}
