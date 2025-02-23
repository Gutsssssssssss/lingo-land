package com.ssafy.a603.lingoland.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter
@NoArgsConstructor
public class SignUpDto {

    @Length(min = 3, max=20)
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{3,20}$")
    private String loginId;

    @Length(min = 3, max=20)
    @NotBlank
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
    private String nickname;

    @Length(min = 6, max=20)
    @NotBlank
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{6,20}$")
    private String password;

    @Length(min = 6, max=20)
    @NotBlank
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{6,20}$")
    private String checkedPassword;
}
