package com.afterschoolclub;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableConfigurationProperties(PolicyProperties.class)
@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
public class AfterSchoolClubApplication {

	public static void main(String[] args) {
		SpringApplication.run(AfterSchoolClubApplication.class, args);
	}
	

    @Bean
    public Executor getAsyncExecutor() {
       ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
       threadPoolTaskExecutor.setThreadNamePrefix("ASC-");
       threadPoolTaskExecutor.setCorePoolSize(10);
       threadPoolTaskExecutor.setMaxPoolSize(30);
       threadPoolTaskExecutor.setKeepAliveSeconds(60);
       threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
       threadPoolTaskExecutor.setQueueCapacity(100);
       threadPoolTaskExecutor.initialize();
       return threadPoolTaskExecutor;
    }
    
}
