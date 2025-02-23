package com.ssafy.a603.lingolandbatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
@EnableJpaAuditing
@Slf4j
public class LingolandBatchApplication implements CommandLineRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    public static void main(String[] args) {
        log.info("app start");
        long startTime = System.currentTimeMillis();
        SpringApplication.run(LingolandBatchApplication.class, args);
        log.info("app end");
        long endTime = System.currentTimeMillis();
        log.info("### app time: " + (endTime - startTime));
    }

    @Override
    public void run(String... args) throws Exception {
        jobLauncher.run(job, new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters());
    }
}
