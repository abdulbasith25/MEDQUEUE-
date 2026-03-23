package com.appointment.service;
import com.appointment.entity.Rating;
import com.appointment.entity.Doctor;
import com.appointment.entity.Patient;
import com.appointment.dto.RatingRequest;
import com.appointment.dto.RatingResponse;
import com.appointment.repository.RatingRepository;
import com.appointment.repository.DoctorRepository;
import com.appointment.repository.PatientRepository;
import com.appointment.exception.ResourceNotFoundException;
import com.appointment.exception.DuplicateResourceException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public RatingResponse addRating(RatingRequest request){
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + request.getPatientId()));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id " + request.getDoctorId()));
        boolean ratingExists = ratingRepository.existsByPatientAndDoctor(patient, doctor);
        if (ratingExists) {
            throw new DuplicateResourceException("Rating already exists for patient " + request.getPatientId() + " and doctor " + request.getDoctorId());
        }
        Rating rating = new Rating();
        rating.setDoctor(doctor);
        rating.setPatient(patient);
        rating.setRating(request.getRating());
        Rating saved = ratingRepository.save(rating);
        return mapToResponse(saved);
    }

    public RatingResponse getDoctorRating(Long doctorId){
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id " + doctorId));
        Double averageRating = ratingRepository.findAverageRatingByDoctor(doctor);
        averageRating = (averageRating != null) ? averageRating : 0.0;
        return new RatingResponse(null, 0, null, doctor.getName(), null, averageRating);
    }

    private RatingResponse mapToResponse(Rating rating){
        return new RatingResponse(
            rating.getId(),
            rating.getRating(),
            rating.getCreatedAt(),
            rating.getDoctor().getName(),
            rating.getPatient().getName(),
            null
        );
    }
}
