package com.ssafy.a603.lingoland.global.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ssafy.a603.lingoland.global.error.entity.ErrorCode;
import com.ssafy.a603.lingoland.global.error.entity.ErrorResponse;
import com.ssafy.a603.lingoland.global.error.exception.BaseException;
import com.ssafy.a603.lingoland.global.error.exception.ForbiddenException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExeptionHandler {

	// 지원하지 않는 Method 요청 시 발생
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<ErrorResponse> handle(HttpRequestMethodNotSupportedException e) {
		log.error("HttpRequestMethodNotSupportedException", e);
		return createErrorResponseEntity(ErrorCode.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(ForbiddenException.class)
	protected ResponseEntity<ErrorResponse> handle(ForbiddenException e) {
		log.error("Forbidden exception to InvalidInputException");
		if (e.getErrorCode() == ErrorCode.GROUP_NOT_LEADER) {
			return createErrorResponseEntity(ErrorCode.INVALID_INPUT_VALUE, ErrorCode.GROUP_NOT_LEADER.getMessage());
		}
		return createErrorResponseEntity(ErrorCode.INVALID_INPUT_VALUE);
	}

	@ExceptionHandler(BaseException.class)
	protected ResponseEntity<ErrorResponse> handle(BaseException e) {
		log.error("BaseException", e);
		return createErrorResponseEntity(e.getErrorCode());
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handle(Exception e) {
		e.printStackTrace();
		log.error("Exception", e);
		return createErrorResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode errorCode) {
		return new ResponseEntity<>(
			ErrorResponse.of(errorCode),
			errorCode.getStatus());
	}

	private ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode errorCode, String message) {
		return new ResponseEntity<>(
			ErrorResponse.of(errorCode, message),
			errorCode.getStatus());
	}
}
