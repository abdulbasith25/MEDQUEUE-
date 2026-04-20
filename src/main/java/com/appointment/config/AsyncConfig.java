package com.appointment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync  // ← Activates @Async processing across the whole app
public class AsyncConfig {

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        return Executors.newFixedThreadPool(3); // 3 is enough for email sending
    }

    @Bean(name = "pushNotificationExecuter")
    public Executor pushNotificationExecuter(){
        return Executors.newFixedThreadPool(2);
    }
}
