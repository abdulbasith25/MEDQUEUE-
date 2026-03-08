package com.appointment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Dynamically size based on available cores (good for I/O bound tasks like email)
        int cores = Runtime.getRuntime().availableProcessors();
        
        executor.setCorePoolSize(cores * 5);  // Base threads
        executor.setMaxPoolSize(cores * 20);  // Max threads when busy (perfect for sending emails)
        executor.setQueueCapacity(500);       // How many emails can wait in queue before max pool is reached
        executor.setThreadNamePrefix("EmailNotify-");
        
        executor.initialize();
        return executor;
    }
}
