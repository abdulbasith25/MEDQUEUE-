package com.appointment.service;

import org.springframework.stereotype.Service;
import com.appointment.entity.Patient;
import com.appointment.service.InsuranceService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsuranceAsyncService {

    private final InsuranceService insuranceService;
    private final PatientRepository patientRepository;

    @Transactional
    @Async
    public void verifyAndUpdate(
        Long patientId,
        String email,
        Long phone
    ){
        boolean isValid = insuranceService.checkValidInsurance(email, phone);
        Patient p = patientRepository.findById(patientId).orElseThrow(()-> new ResourceNotFoundException("patient not found"));
        p.setInsuranceVerified(isValid);
        patientRepository.save(p);

    }
}
