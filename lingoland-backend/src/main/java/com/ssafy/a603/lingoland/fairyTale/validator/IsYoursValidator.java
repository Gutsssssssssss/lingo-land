package com.ssafy.a603.lingoland.fairyTale.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ssafy.a603.lingoland.fairyTale.entity.FairyTaleMemberId;
import com.ssafy.a603.lingoland.fairyTale.repository.FairyTaleMemberRepository;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IsYoursValidator implements Validator {
	private final FairyTaleMemberRepository fairyTaleMemberRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return CustomUserDetails.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

	}

	public void validate(Object target, Errors errors, Integer fairyTaleId) {
		CustomUserDetails user = (CustomUserDetails)target;
		if (!fairyTaleMemberRepository.existsByFairyTaleIdAndMemberId(fairyTaleId, user.getMemberId())) {
			errors.rejectValue("FairyTale", "당신 소유의 동화가 아닙니다.");
		}

		if (!fairyTaleMemberRepository.findIsVisibleById(
			FairyTaleMemberId.builder()
				.fairyTaleId(fairyTaleId)
				.memberId(user.getMemberId())
				.build())
		) {
			errors.rejectValue("FairyTale", "존재하지 않는 동화입니다.");
		}
	}
}
