package com.ssafy.a603.lingolandbatch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;



@Configuration
public class WebClientConfig {

    @Value("${spring.chatGPTKey}")
    String apiKey;

    @Bean
    public WebClient.Builder webClientBuilder(){

        return WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json; charset=utf-8");

    }
}

