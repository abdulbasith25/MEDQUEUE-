package com.appointment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync  
public class AsyncConfig {

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("Notification-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "pushNotificationExecutor")
    public Executor pushNotificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(19);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("PushNotification-");
        executor.initialize();
        return executor;
    }
    // @Bean(name = "callableTaskExecutor")
    // ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    // execcutor.setCorePoolSize(2);
    // executor.setMaxPoolSize(`0);
    // executor.setQueueCapacity(100);
    // executor.setThreadNameePrefix("callablethread");
    // executor.initialize();
    // return executor;

    @Bean(name = "InsuranceExecutor")
    public Executor insuranceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Insurance-");
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "QupdateExecutor")
    public ThreadPoolTaskExecutor QupdateExecutor(){
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("Qtaskexecutor");
    executor.initialize();
    return executor;
}
