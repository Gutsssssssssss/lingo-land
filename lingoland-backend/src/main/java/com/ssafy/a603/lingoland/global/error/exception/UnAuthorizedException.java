package com.ssafy.a603.lingoland.global.error.exception;

import com.ssafy.a603.lingoland.global.error.entity.ErrorCode;

public class UnAuthorizedException extends BaseException {
	public UnAuthorizedException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}

	public UnAuthorizedException() {
		super(ErrorCode.UNAUTHORIZED);
	}
}
