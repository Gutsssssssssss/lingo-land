package com.ssafy.a603.lingoland.global.error.exception;

import com.ssafy.a603.lingoland.global.error.entity.ErrorCode;

public class InvalidInputException extends BaseException {
	public InvalidInputException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}

	public InvalidInputException() {
		super(ErrorCode.INVALID_INPUT_VALUE);
	}
}