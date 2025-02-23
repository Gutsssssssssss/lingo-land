package com.ssafy.a603.lingoland.group.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ssafy.a603.lingoland.group.dto.CreateGroupDTO;
import com.ssafy.a603.lingoland.group.repository.GroupRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateGroupValidator implements Validator {

	private final GroupRepository groupRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return CreateGroupDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CreateGroupDTO request = (CreateGroupDTO)target;
		if (groupRepository.existsByName(request.name())) {
			errors.rejectValue("name", "이미 존재하는 이름입니다.");
		}
	}
}