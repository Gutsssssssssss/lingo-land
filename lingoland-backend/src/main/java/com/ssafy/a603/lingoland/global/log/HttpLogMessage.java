package com.ssafy.a603.lingoland.global.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public record HttpLogMessage(
        String httpMethod,
        String requestUri,
        HttpStatus httpStatus,
        double elapsedTime,
        String headers,
        String requestParam,
        String requestBody,
        String responseBody
) {
    public static HttpLogMessage createInstance(ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper, double elapsedTime, ObjectMapper objectMapper) throws JsonProcessingException {
        return new HttpLogMessage(
                requestWrapper.getMethod(),
                requestWrapper.getRequestURI(),
                HttpStatus.valueOf(responseWrapper.getStatus()),
                elapsedTime,
                getRequestHeaders(requestWrapper, objectMapper),
                getRequestParams(requestWrapper),
                getRequestBody(requestWrapper),
                getResponseBody(responseWrapper)
        );
    }

    public String toPrettierLog() {
        return String.format("""
                
                {
                    "REQUEST" : "%s %s %s (%.3f)",
                    "HEADERS" : %s,
                    "REQUEST_PARAM" : %s,
                    "REQUEST_BODY" : %s,
                    "RESPONSE_BODY" : %s
                }
                """,
                this.httpMethod,
                this.requestUri,
                this.httpStatus,
                this.elapsedTime,
                this.headers,
                this.requestParam,
                this.requestBody,
                this.responseBody
        );
    }

    private static String getRequestBody(ContentCachingRequestWrapper requestWrapper) {
        return new String(requestWrapper.getContentAsByteArray());
    }

    private static String getRequestParams(ContentCachingRequestWrapper requestWrapper) {
        return requestWrapper.getQueryString();
    }

    private static String getRequestHeaders(ContentCachingRequestWrapper requestWrapper, ObjectMapper objectMapper) throws JsonProcessingException {
        Enumeration<String> headerNames = requestWrapper.getHeaderNames();
        Map<String, String> headersMap = new HashMap<>();

        while(headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headersMap.put(headerName, requestWrapper.getHeader(headerName));
        }

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(headersMap);
    }

    private static String getResponseBody(ContentCachingResponseWrapper responseWrapper) {
        return new String(responseWrapper.getContentAsByteArray());
    }
}
