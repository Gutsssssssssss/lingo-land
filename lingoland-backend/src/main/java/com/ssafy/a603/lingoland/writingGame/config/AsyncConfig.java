package com.ssafy.a603.lingoland.writingGame.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import com.ssafy.a603.lingoland.global.error.entity.ErrorCode;
import com.ssafy.a603.lingoland.global.error.exception.BaseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
	@Override
	@Bean(name = "myExecutor")
	public ExecutorService getAsyncExecutor() {
		ThreadFactory factory = Thread.ofVirtual()
			.name("virtual-thread", 1)
			.uncaughtExceptionHandler(
				(t, e) -> {
					log.error("Uncaught exception in thread " + t.getName() + ": " + e.getMessage());
					throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
				})
			.factory();
		return Executors.newThreadPerTaskExecutor(factory);
	}

	@Bean(name = "asyncExecutor")
	public AsyncTaskExecutor applicationTaskExecutor() {
		return new TaskExecutorAdapter(getAsyncExecutor());
	}
}