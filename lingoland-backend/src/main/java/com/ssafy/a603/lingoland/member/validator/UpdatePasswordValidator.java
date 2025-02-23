package com.ssafy.a603.lingoland.member.validator;

import com.ssafy.a603.lingoland.member.dto.UpdatePasswordDto;
import com.ssafy.a603.lingoland.member.entity.Member;
import com.ssafy.a603.lingoland.member.repository.MemberRepository;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class UpdatePasswordValidator implements Validator {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return UpdatePasswordDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }


    public void validate(Object target, Errors errors, CustomUserDetails customUserDetails) {
        UpdatePasswordDto updatePasswordDto = (UpdatePasswordDto) target;
        Member member = memberRepository.findByLoginId(customUserDetails.getUsername()).orElseThrow(() -> new NoSuchElementException("존재하지 않은 유저입니다"));
        if(!updatePasswordDto.password().equals(updatePasswordDto.checkedPassword())) {
            errors.rejectValue("checkedPassword", "비밀번호가 일치하지 않습니다.");
            return;
        }
        if(passwordEncoder.matches(updatePasswordDto.password(), member.getPassword())) {
            errors.rejectValue("password", "기존의 비밀번호와 일치합니다.");
        }
    }

}
