package com.ssafy.a603.lingoland.group.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ssafy.a603.lingoland.group.dto.CreateGroupDTO;

@Component
public class GroupPasswordValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return CreateGroupDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CreateGroupDTO createGroupDTO = (CreateGroupDTO)target;

		// 비밀번호가 숫자 4자리인지 확인
		if (createGroupDTO.password() == null) {
			errors.rejectValue("password", "비밀번호는 반드시 입력해야 합니다.");
		}

		if (createGroupDTO.password() < 1000 || createGroupDTO.password() > 9999) {
			errors.rejectValue("password", "비밀번호는 4자리 숫자여야 합니다.");
		}
	}
}
