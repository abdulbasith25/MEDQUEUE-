package com.appointment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
@EnableScheduling
public class AppointmentSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppointmentSystemApplication.class, args);
    }
}
