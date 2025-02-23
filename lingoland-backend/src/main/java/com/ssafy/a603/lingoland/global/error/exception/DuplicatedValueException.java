package com.ssafy.a603.lingoland.global.error.exception;

import com.ssafy.a603.lingoland.global.error.entity.ErrorCode;

public class DuplicatedValueException extends BaseException {
	public DuplicatedValueException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}

	public DuplicatedValueException() {
		super(ErrorCode.DUPLICATED_VALUE);
	}
}
