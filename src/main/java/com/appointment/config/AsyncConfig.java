package com.appointment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync  // ← Activates @Async processing across the whole app
public class AsyncConfig {

    /**
     * Dedicated thread pool for sending email notifications.
     * Named "notificationExecutor" to match @Async("notificationExecutor")
     * in EmailNotificationService.
     *
     * Why a separate pool?
     *   - Email is slow (network I/O). We don't want it sharing threads
     *     with other tasks.
     *   - If the mail server is down, only these 3 threads are blocked.
     *     The rest of the app keeps running normally.
     */
    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        return Executors.newFixedThreadPool(3); // 3 is enough for email sending
    }

    @Bean(name = "pushNotificationExecuter")
    public Executor pushNotificationExecuter(){
        return Executors.newFixedThreadPool(2);
    }
}
