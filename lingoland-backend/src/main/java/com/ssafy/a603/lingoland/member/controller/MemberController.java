package com.ssafy.a603.lingoland.member.controller;

import com.ssafy.a603.lingoland.member.dto.GetMemberInfoDto;
import com.ssafy.a603.lingoland.member.dto.SignUpDto;
import com.ssafy.a603.lingoland.member.dto.UpdateNicknameDto;
import com.ssafy.a603.lingoland.member.dto.UpdatePasswordDto;
import com.ssafy.a603.lingoland.member.entity.Member;
import com.ssafy.a603.lingoland.member.security.CurrentUser;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;
import com.ssafy.a603.lingoland.member.service.MemberService;
import com.ssafy.a603.lingoland.member.validator.SignUpValidator;
import com.ssafy.a603.lingoland.member.validator.UpdatePasswordValidator;
import com.ssafy.a603.lingoland.member.validator.UpdateProfileImageValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class MemberController {

    private final MemberService memberService;
    private final SignUpValidator signUpValidator;
    private final UpdatePasswordValidator updatePasswordValidator;
    private final UpdateProfileImageValidator updateProfileImageValidator;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpDto signUpRequest, BindingResult bindingResult) {
        signUpValidator.validate(signUpRequest, bindingResult);
        if(bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getCode)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        Member member = memberService.saveNewMember(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @GetMapping("/check/{loginId}")
    public ResponseEntity<?> checkIdDuplication(@PathVariable(value = "loginId") String loginId){
        if(memberService.checkIdDuplication(loginId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }

    @GetMapping
    public ResponseEntity<?> getMemberInfo(@CurrentUser CustomUserDetails customUserDetails) {
        GetMemberInfoDto getMemberInfoDto = memberService.getMemberInfo(customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).body(getMemberInfoDto);
    }

    @GetMapping("/{loginId}")
    public ResponseEntity<?> getMemberInfoByLoginId(@PathVariable(value = "loginId") String loginId){
        GetMemberInfoDto getMemberInfoDto = memberService.getMemberInfoByLoginId(loginId);
        return ResponseEntity.status(HttpStatus.OK).body(getMemberInfoDto);
    }

    @PutMapping("/nickname")
    public ResponseEntity<?> updateNickname(@Valid @RequestBody UpdateNicknameDto updateNicknameDto,
                                            @CurrentUser CustomUserDetails customUserDetails) {
        memberService.updateNickname(updateNicknameDto, customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto,
                                            @CurrentUser CustomUserDetails customUserDetails,
                                            BindingResult bindingResult) {
        updatePasswordValidator.validate(updatePasswordDto, bindingResult, customUserDetails);
        if(bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getCode)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        memberService.updatePassword(updatePasswordDto, customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping(path = "/profile-image", produces = "application/json", consumes = "multipart/form-data")
    public ResponseEntity<?> updateProfileImage(@RequestPart(value = "profileImage") MultipartFile profileImage,
                                                @CurrentUser CustomUserDetails customUserDetails) {
        memberService.updateProfileImage(profileImage, customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
