package com.ssafy.a603.lingoland.member.service;

import java.util.NoSuchElementException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.a603.lingoland.member.dto.GetMemberInfoDto;
import com.ssafy.a603.lingoland.member.dto.SignUpDto;
import com.ssafy.a603.lingoland.member.dto.UpdateNicknameDto;
import com.ssafy.a603.lingoland.member.dto.UpdatePasswordDto;
import com.ssafy.a603.lingoland.member.entity.Member;
import com.ssafy.a603.lingoland.member.entity.Rank;
import com.ssafy.a603.lingoland.member.entity.Role;
import com.ssafy.a603.lingoland.member.repository.MemberRepository;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;
import com.ssafy.a603.lingoland.util.ImgUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements UserDetailsService, MemberService {
	private static final String MEMBER_IMAGE_PATH = "member";
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final ImgUtils imgUtils;

	@Transactional
	public Member saveNewMember(SignUpDto signUpRequest) {
		Member member = Member.builder()
			.loginId(signUpRequest.getLoginId())
			.nickname(signUpRequest.getNickname())
			.password(passwordEncoder.encode(signUpRequest.getPassword()))
			.profileImage(imgUtils.getImagePathWithDefaultImage(MEMBER_IMAGE_PATH))
			.rank(Rank.CHAMBONG)
			.role(Role.ROLE_USER)
			.build();
		return memberRepository.save(member);
	}

	@Override
	public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
		Member member = getMember(loginId);
		return new CustomUserDetails(member);
	}

	@Transactional
	public void addRefreshToken(String loginId, String refresh) {
		Member member = getMember(loginId);
		member.updateRefreshToken(refresh);
	}

	@Transactional
	public void deleteRefreshToken(String loginId) {
		Member member = getMember(loginId);
		member.updateRefreshToken(null);
	}

	public Boolean checkExistRefreshToken(String loginId) {
		Member member = getMember(loginId);
		return member.getRefreshToken() != null;
	}

	public GetMemberInfoDto getMemberInfo(CustomUserDetails customUserDetails) {
		Member member = getMember(customUserDetails.getUsername());
		return createMemberInfoDto(member);
	}

	private static GetMemberInfoDto createMemberInfoDto(Member member) {
		return GetMemberInfoDto.builder()
			.nickname(member.getNickname())
			.profileImage(member.getProfileImage())
			.maxExperiencePoint(member.getRank().getMaxExperience())
			.experiencePoint(member.getExperiencePoint())
			.rank(member.getRank().getGrade())
			.build();
	}

	@Override
	public GetMemberInfoDto getMemberInfoByLoginId(String loginId) {
		Member member = getMember(loginId);
		return GetMemberInfoDto.builder()
			.nickname(member.getNickname())
			.profileImage(member.getProfileImage())
			.maxExperiencePoint(member.getRank().getMaxExperience())
			.experiencePoint(member.getExperiencePoint())
			.rank(member.getRank().getGrade())
			.build();
	}

	@Transactional
	public void updateNickname(UpdateNicknameDto updateNicknameDto, CustomUserDetails customUserDetails) {
		Member member = getMember(customUserDetails.getUsername());
		member.updateNickname(updateNicknameDto.nickname());
	}

	@Transactional
	@Override
	public void updatePassword(UpdatePasswordDto updatePasswordDto, CustomUserDetails customUserDetails) {
		Member member = getMember(customUserDetails.getUsername());
		member.updatePassword(passwordEncoder.encode(updatePasswordDto.password()));
	}

	@Transactional
	@Override
	public void updateProfileImage(MultipartFile profileImage, CustomUserDetails customUserDetails) {
		Member member = getMember(customUserDetails.getUsername());
		imgUtils.deleteImage(member.getProfileImage());
		member.updateProfileImage(imgUtils.saveImage(profileImage, MEMBER_IMAGE_PATH));
	}

	private Member getMember(String loginId) {
		return memberRepository.findByLoginId(loginId).orElseThrow(() -> new NoSuchElementException("존재하지 않은 유저입니다"));
	}

	public boolean checkIdDuplication(String loginId) {
		return memberRepository.existsByLoginId(loginId);
	}
}
