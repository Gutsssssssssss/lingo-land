package com.ssafy.a603.lingoland.member.service;

import com.ssafy.a603.lingoland.member.dto.GetMemberInfoDto;
import com.ssafy.a603.lingoland.member.dto.SignUpDto;
import com.ssafy.a603.lingoland.member.dto.UpdateNicknameDto;
import com.ssafy.a603.lingoland.member.dto.UpdatePasswordDto;
import com.ssafy.a603.lingoland.member.entity.Member;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    Member saveNewMember(SignUpDto signUpRequest);

    void addRefreshToken(String loginId, String refresh);

    void deleteRefreshToken(String loginId);

    Boolean checkExistRefreshToken(String loginId);

    GetMemberInfoDto getMemberInfo(CustomUserDetails customUserDetails);

    void updateNickname(UpdateNicknameDto updateNicknameDto, CustomUserDetails customUserDetails);

    boolean checkIdDuplication(String loginId);

    void updatePassword(UpdatePasswordDto updatePasswordDto, CustomUserDetails customUserDetails);

    void updateProfileImage(MultipartFile profileImage, CustomUserDetails customUserDetails);

    GetMemberInfoDto getMemberInfoByLoginId(String loginId);
}
