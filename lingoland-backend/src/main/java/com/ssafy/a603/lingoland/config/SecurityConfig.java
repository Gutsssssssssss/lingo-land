package com.ssafy.a603.lingoland.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.a603.lingoland.member.security.CustomLogoutFilter;
import com.ssafy.a603.lingoland.member.security.JWTFilter;
import com.ssafy.a603.lingoland.member.security.JWTUtil;
import com.ssafy.a603.lingoland.member.security.LoginFilter;
import com.ssafy.a603.lingoland.member.service.MemberServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	@Value("${frontend-url}")
	private String frontendUrl;
	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;
	private final MemberServiceImpl memberService;
	private final ObjectMapper objectMapper;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(AbstractHttpConfigurer::disable);

		http.sessionManagement((session) -> {
			session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		});

		http.httpBasic(AbstractHttpConfigurer::disable);
		http.formLogin(AbstractHttpConfigurer::disable);

		http.authorizeHttpRequests((authorizeRequests) -> authorizeRequests
			.requestMatchers("/", "/error", "/api/v1/users/sign-up", "/api/v1/login", "/api/v1/users/**",
				"/api/v1/sessions", "/api/v1/sessions/**", "/api/v1/problems").permitAll()
			.requestMatchers("/api/v1/reissue").permitAll()
			.anyRequest().authenticated());

		http.addFilterBefore(new JWTFilter(jwtUtil, memberService), LoginFilter.class);

		http.addFilterAt(
			new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, memberService, objectMapper),
			UsernamePasswordAuthenticationFilter.class);

		http.addFilterBefore(new CustomLogoutFilter(jwtUtil, memberService), LogoutFilter.class);

		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		configuration.setExposedHeaders(Arrays.asList("Authorization"));
		configuration.setAllowedOriginPatterns(Arrays.asList(frontendUrl,"https://i11a603.p.ssafy.io"
		, "http://i11a603.p.ssafy.io"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
