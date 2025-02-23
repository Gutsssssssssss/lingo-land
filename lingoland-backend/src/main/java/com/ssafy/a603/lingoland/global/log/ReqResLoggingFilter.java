package com.ssafy.a603.lingoland.global.log;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ReqResLoggingFilter extends OncePerRequestFilter {

	private static final String REQUEST_ID = "request_id";
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		ContentCachingRequestWrapper cachingRequestWrapper = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper cachingResponseWrapper = new ContentCachingResponseWrapper(response);

		String requestId = UUID.randomUUID().toString().substring(0, 8);
		MDC.put(REQUEST_ID, requestId);

		long start = System.currentTimeMillis();
		filterChain.doFilter(cachingRequestWrapper, cachingResponseWrapper);
		long end = System.currentTimeMillis();

		double elapsedTme = (end - start) / 1000.0;

		try {
			log.info(HttpLogMessage.createInstance(
				cachingRequestWrapper,
				cachingResponseWrapper,
				elapsedTme,
				objectMapper
			).toPrettierLog());
		} catch (Exception e) {
			log.error("Logging 실패");
		}

		MDC.remove(REQUEST_ID);
        
		cachingResponseWrapper.copyBodyToResponse();
	}
}
