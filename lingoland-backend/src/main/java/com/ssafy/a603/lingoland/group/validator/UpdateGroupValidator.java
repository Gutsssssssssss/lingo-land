package com.ssafy.a603.lingoland.group.validator;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ssafy.a603.lingoland.group.dto.UpdateGroupDTO;
import com.ssafy.a603.lingoland.group.entity.Group;
import com.ssafy.a603.lingoland.group.repository.GroupRepository;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateGroupValidator implements Validator {
	private final GroupRepository groupRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return UpdateGroupDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

	}

	public void validate(Object target, Errors errors, CustomUserDetails customUserDetails) {
		UpdateGroupDTO request = (UpdateGroupDTO)target;

		Optional<Group> optionalGroup = groupRepository.findById(request.id());
		if (optionalGroup.isEmpty()) {
			errors.rejectValue("Group", "그룹이 존재하지 않습니다.");
		}
		Group group = optionalGroup.get();
		if (group.isDeleted()) {
			errors.rejectValue("Group", "그룹이 존재하지 않습니다.");
		}
		if (group.getLeader().getId() != customUserDetails.getMemberId()) {
			errors.rejectValue("GroupLeader", "그룹 리더가 아닙니다.");
		}

		if (request.password() != null && (request.password() < 1000 || request.password() > 9999)) {
			errors.rejectValue("Password", "비밀번호는 4자리 숫자여야 합니다.");
		}
	}
}
