package com.ssafy.a603.lingoland.writingGame.config;

import java.time.Duration;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RestClientConfig {

	@Bean
	public RestClient restClient() {
		ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
			.withReadTimeout(Duration.ofMinutes(3));
		ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(settings);
		return RestClient.builder()
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.defaultStatusHandler(HttpStatusCode::is4xxClientError,
				(request, response) -> {
					log.error("Client Error Code={}", response.getStatusCode());
					log.error("Client Error Message={}", new String(response.getBody().readAllBytes()));
				})
			.defaultStatusHandler(HttpStatusCode::is5xxServerError,
				(request, response) -> {
					log.error("Server Error Code={}", response.getStatusCode());
					log.error("Server Error Message={}", new String(response.getBody().readAllBytes()));
				})
			.requestFactory(requestFactory)
			.build();
	}
}
