package com.ssafy.a603.lingoland.member.validator;

import com.ssafy.a603.lingoland.member.dto.SignUpDto;
import com.ssafy.a603.lingoland.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpValidator implements Validator {

    private final MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return SignUpDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpDto request = (SignUpDto) target;
        if(memberRepository.existsByLoginId(request.getLoginId())) {
            errors.rejectValue("loginId", "이미 존재하는 아이디입니다.");
            return;
        }
        if(!request.getCheckedPassword().equals(request.getPassword())) {
            errors.rejectValue("checkedPassword", "비밀번호를 다시 확인해주세요.");
        }
    }
}
