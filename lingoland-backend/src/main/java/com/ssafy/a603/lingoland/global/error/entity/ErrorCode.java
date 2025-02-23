package com.ssafy.a603.lingoland.global.error.entity;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M1", "존재하지 않는 사용자입니다."),
	MEMBER_DUPLICATED_VALUE(HttpStatus.BAD_REQUEST, "M2", "이미 존재하는 사용자입니다."),
	MEMBER_FORBIDDEN(HttpStatus.FORBIDDEN, "M3", "권한이 없는 사용자입니다."),
	MEMBER_INVALID_INPUT(HttpStatus.BAD_REQUEST, "M4", "올바르지 않은 입력값입니다."),

	GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "G1", "존재하지 않는 그룹입니다."),
	GROUP_DUPLICATED_VALUE(HttpStatus.BAD_REQUEST, "G2", "이미 존재하는 그룹입니다."),
	GROUP_INVALID_INPUT(HttpStatus.BAD_REQUEST, "G3", "올바르지 않은 입력값입니다."),
	GROUP_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "G4", "비밀번호가 일치하지 않습니다."),
	GROUP_NOT_LEADER(HttpStatus.FORBIDDEN, "G5", "리더가 아닙니다."),

	FAIRY_TALE_NOT_FOUND(HttpStatus.NOT_FOUND, "F1", "존재하지 않는 동화입니다."),
	FAIRY_TALE_DUPLICATED_VALUE(HttpStatus.BAD_REQUEST, "F2", "이미 존재하는 동화입니다."),

	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E1", "올바르지 않은 입력값입니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "E2", "잘못된 HTTP 메서드를 호출했습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E3", "서버 에러가 발생했습니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "E4", "존재하지 않는 엔티티입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E5", "인증되지 않은 사용자입니다."),
	DUPLICATED_VALUE(HttpStatus.BAD_REQUEST, "E6", "이미 존재하는 엔티티입니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "E7", "권한이 없는 사용자입니다."),
	JSON_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E8", "JSON 변환에 실패했습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	ErrorCode(final HttpStatus status, final String code, final String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}
