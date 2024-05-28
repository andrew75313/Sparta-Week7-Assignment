package com.sparta.scheduleradvanced;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ScheduleradvancedApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduleradvancedApplication.class, args);
    }

}
