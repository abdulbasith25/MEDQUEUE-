package com.appointment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InsuranceService {

    /**
     * Mocks an external Insurance API call.
     */
    public boolean checkValidInsurance(String email, String phone) {
        log.info("[INSURANCE] Connecting to insurance provider for {}...", email);
        try {
            // Simulate network delay of an external API
            Thread.sleep(2000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Mock Logic: Always valid unless the email contains "invalid"
        boolean isValid = email != null && !email.contains("invalid");
        log.info("[INSURANCE] Verification result for {}: {}", email, isValid ? "SUCCESS" : "FAILED");
        
        return isValid;
    }
}
