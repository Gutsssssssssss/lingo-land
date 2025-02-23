package com.ssafy.a603.lingoland.global.error.exception;

import com.ssafy.a603.lingoland.global.error.entity.ErrorCode;

public class ForbiddenException extends BaseException {
	public ForbiddenException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}

	public ForbiddenException() {
		super(ErrorCode.FORBIDDEN);
	}
}