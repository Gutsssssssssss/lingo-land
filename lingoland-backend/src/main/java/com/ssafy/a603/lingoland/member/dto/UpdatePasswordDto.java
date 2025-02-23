package com.ssafy.a603.lingoland.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record UpdatePasswordDto(
        @Length(min = 6, max=20) @NotBlank @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{6,20}$") String password,
        @Length(min = 6, max=20) @NotBlank @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{6,20}$") String checkedPassword) {
}
