package com.ssafy.a603.lingoland.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.ssafy.a603.lingoland.global.listener.AuditorAwareImpl;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

	@Bean
	AuditorAware<String> auditorAware() {
		return new AuditorAwareImpl();
	}
}
