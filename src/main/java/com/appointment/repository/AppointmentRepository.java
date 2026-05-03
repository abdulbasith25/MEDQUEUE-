package com.appointment.repository;
import com.appointment.dto.DoctorQueueStatusResponse;
import com.appointment.entity.Appointment;
import com.appointment.entity.AppointmentStatus;
import com.appointment.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.appointment.entity.Patient;
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

    // Single query — replaces N×5 individual queries for the admin dashboard
    @Query("""
        SELECT new com.appointment.dto.DoctorQueueStatusResponse(
            d.id, d.name, d.specialization, d.available,
            SUM(CASE WHEN a.status = com.appointment.entity.AppointmentStatus.BOOKED   THEN 1L ELSE 0L END),
            SUM(CASE WHEN a.status = com.appointment.entity.AppointmentStatus.DONE      THEN 1L ELSE 0L END),
            SUM(CASE WHEN a.status = com.appointment.entity.AppointmentStatus.SKIPPED   THEN 1L ELSE 0L END),
            SUM(CASE WHEN a.status = com.appointment.entity.AppointmentStatus.CANCELLED THEN 1L ELSE 0L END),
            MAX(CASE WHEN a.status = com.appointment.entity.AppointmentStatus.DONE THEN a.tokenNumber ELSE null END)
        )
        FROM Doctor d
        LEFT JOIN Appointment a ON a.doctor.id = d.id AND a.date = :date
        GROUP BY d.id, d.name, d.specialization, d.available
        """)
    List<DoctorQueueStatusResponse> findAllDoctorQueueStatus(@Param("date") LocalDate date);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByStatusAndDateLessThanEqual(AppointmentStatus status, LocalDate date);
    List<Appointment> findByDoctorAndStatus(Doctor doctor, AppointmentStatus status);
    Optional<Appointment> findFirstByPatientAndDateOrderByTokenNumberDesc(Patient patient, LocalDate date);
    List<Appointment> findByDoctorAndStatusAndDate(Doctor doctor, AppointmentStatus status, LocalDate date);
    @Query("""
SELECT new com.appointment.dto.DailyStatsResponse(
    :date,
    COUNT(a),
    SUM(CASE WHEN a.status = com.appointment.entity.AppointmentStatus.BOOKED   THEN 1L ELSE 0L END),
    SUM(CASE WHEN a.status = com.appointment.entity.AppointmentStatus.DONE      THEN 1L ELSE 0L END),
    SUM(CASE WHEN a.status = com.appointment.entity.AppointmentStatus.SKIPPED   THEN 1L ELSE 0L END),
    SUM(CASE WHEN a.status = com.appointment.entity.AppointmentStatus.CANCELLED THEN 1L ELSE 0L END)
)
FROM Appointment a
WHERE a.date = :date
""")
DailyStatsResponse getDailyStats(@Param("date") LocalDate date);
    
}
