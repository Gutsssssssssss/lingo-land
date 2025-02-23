package com.ssafy.a603.lingoland.global.error.exception;

import com.ssafy.a603.lingoland.global.error.entity.ErrorCode;

public class NotFoundException extends BaseException {
	public NotFoundException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}

	public NotFoundException() {
		super(ErrorCode.NOT_FOUND);
	}
}
