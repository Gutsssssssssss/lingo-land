package com.ssafy.a603.lingoland.fairyTale.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.a603.lingoland.fairyTale.dto.FairyTaleListResponseDTO;
import com.ssafy.a603.lingoland.fairyTale.dto.UpdateFairyTaleRequestDTO;
import com.ssafy.a603.lingoland.fairyTale.entity.FairyTale;
import com.ssafy.a603.lingoland.fairyTale.entity.FairyTaleMember;
import com.ssafy.a603.lingoland.fairyTale.entity.FairyTaleMemberId;
import com.ssafy.a603.lingoland.fairyTale.repository.FairyTaleMemberRepository;
import com.ssafy.a603.lingoland.fairyTale.repository.FairyTaleRepository;
import com.ssafy.a603.lingoland.global.error.entity.ErrorCode;
import com.ssafy.a603.lingoland.global.error.exception.NotFoundException;
import com.ssafy.a603.lingoland.member.entity.Member;
import com.ssafy.a603.lingoland.member.repository.MemberRepository;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FairyTaleServiceImpl implements FairyTaleService {
	private final FairyTaleRepository fairyTaleRepository;
	private final MemberRepository memberRepository;
	private final FairyTaleMemberRepository fairyTaleMemberRepository;

	@Override
	@Transactional
	public FairyTale createFairyTale(String title, String cover, String summary, List<FairyTale.Story> content,
		List<String> writers) {
		log.info("Creating fairy tale with title: {}", title);

		List<Member> members;
		try {
			members = writers.stream()
				.map(memberLoginId -> memberRepository.findByLoginId(memberLoginId)
					.orElseThrow(() -> {
						log.error("Member not found with login ID: {}", memberLoginId);
						return new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
					}))
				.toList();
		} catch (NotFoundException e) {
			log.error("Error occurred while fetching members: {}", e.getMessage(), e);
			throw e;
		}

		FairyTale fairyTale;
		try {
			fairyTale = fairyTaleRepository.save(FairyTale.builder()
				.title(title)
				.cover(cover)
				.summary(summary)
				.content(content)
				.build());
		} catch (Exception e) {
			log.error("Error occurred while saving fairy tale: {}", e.getMessage(), e);
			throw e;
		}

		for (Member member : members) {
			try {
				FairyTaleMember fairyTaleMember = FairyTaleMember.builder()
					.fairyTale(fairyTale)
					.member(member)
					.build();
				fairyTaleMemberRepository.save(fairyTaleMember);
			} catch (Exception e) {
				log.error("Error occurred while saving fairy tale member for member ID {}: {}", member.getId(),
					e.getMessage(), e);
				throw e;
			}
		}

		log.info("Fairy tale created successfully with ID: {}", fairyTale.getId());
		return fairyTale;
	}

	@Override
	@Transactional(readOnly = true)
	public List<FairyTaleListResponseDTO> findFairyTaleListByLoginId(String loginId) {
		log.info("Finding fairy tales for member with login ID: {}", loginId);

		Member member;
		try {
			member = memberRepository.findByLoginId(loginId)
				.orElseThrow(() -> {
					log.error("Member not found with login ID: {}", loginId);
					return new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
				});
		} catch (NotFoundException e) {
			log.error("Error occurred while fetching member by login ID {}: {}", loginId, e.getMessage(), e);
			throw e;
		}

		List<FairyTaleListResponseDTO> fairyTales;
		try {
			fairyTales = fairyTaleRepository.findAllFairyTalesByMemberId(member.getId());
		} catch (Exception e) {
			log.error("Error occurred while fetching fairy tales for member ID {}: {}", member.getId(), e.getMessage(),
				e);
			throw e;
		}

		log.info("Found {} fairy tales for member with login ID: {}", fairyTales.size(), loginId);
		return fairyTales;
	}

	@Override
	@Transactional(readOnly = true)
	public List<FairyTaleListResponseDTO> findFairyTaleListByLoginId(CustomUserDetails customUserDetails) {
		log.info("Finding fairy tales for member with ID: {}", customUserDetails.getMemberId());

		List<FairyTaleListResponseDTO> fairyTales;
		try {
			fairyTales = fairyTaleRepository.findAllFairyTalesByMemberId(customUserDetails.getMemberId());
		} catch (Exception e) {
			log.error("Error occurred while fetching fairy tales for member ID {}: {}", customUserDetails.getMemberId(),
				e.getMessage(), e);
			throw e;
		}

		log.info("Found {} fairy tales for member with ID: {}", fairyTales.size(), customUserDetails.getMemberId());
		return fairyTales;
	}

	@Override
	@Transactional(readOnly = true)
	public FairyTale findFairyTaleById(Integer fairyTaleId) {
		log.info("Finding fairy tale with ID: {}", fairyTaleId);

		FairyTale fairyTale;
		try {
			fairyTale = fairyTaleRepository.findById(fairyTaleId)
				.orElseThrow(() -> {
					log.error("Fairy tale not found with ID: {}", fairyTaleId);
					return new NotFoundException(ErrorCode.FAIRY_TALE_NOT_FOUND);
				});
		} catch (NotFoundException e) {
			log.error("Error occurred while fetching fairy tale with ID {}: {}", fairyTaleId, e.getMessage(), e);
			throw e;
		}

		log.info("Found fairy tale with ID: {}", fairyTale.getId());
		return fairyTale;
	}

	@Override
	@Transactional
	public void updateFairyTale(UpdateFairyTaleRequestDTO request) {
		log.info("Updating fairy tale with ID: {}", request.id());

		FairyTale fairyTale;
		try {
			fairyTale = fairyTaleRepository.findById(request.id())
				.orElseThrow(() -> {
					log.error("Fairy tale not found with ID: {}", request.id());
					return new NotFoundException(ErrorCode.FAIRY_TALE_NOT_FOUND);
				});
		} catch (NotFoundException e) {
			log.error("Error occurred while fetching fairy tale with ID {}: {}", request.id(), e.getMessage(), e);
			throw e;
		}

		try {
			fairyTale.update(request);
		} catch (Exception e) {
			log.error("Error occurred while updating fairy tale with ID {}: {}", fairyTale.getId(), e.getMessage(), e);
			throw e;
		}

		log.info("Fairy tale with ID: {} updated successfully", fairyTale.getId());
	}

	@Override
	@Transactional
	public void fairyTaleInvisible(Integer fairyTaleId, CustomUserDetails customUserDetails) {
		log.info("Setting fairy tale with ID: {} to invisible for member ID: {}", fairyTaleId,
			customUserDetails.getMemberId());

		Member member;
		try {
			member = getMemberFromUserDetails(customUserDetails);
		} catch (NotFoundException e) {
			log.error("Error occurred while fetching member from user details for member ID {}: {}",
				customUserDetails.getMemberId(), e.getMessage(), e);
			throw e;
		}

		FairyTaleMemberId fairyTaleMemberId = FairyTaleMemberId.builder()
			.fairyTaleId(fairyTaleId)
			.memberId(member.getId())
			.build();

		FairyTaleMember fairyTaleMember;
		try {
			fairyTaleMember = fairyTaleMemberRepository.findById(fairyTaleMemberId)
				.orElseThrow(() -> {
					log.error("Fairy tale member not found with ID: {}", fairyTaleMemberId);
					return new NotFoundException(ErrorCode.FAIRY_TALE_NOT_FOUND);
				});
			fairyTaleMember.invisible();
		} catch (NotFoundException e) {
			log.error("Error occurred while fetching fairy tale member with ID {}: {}", fairyTaleMemberId,
				e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("Error occurred while setting fairy tale invisible for member ID {}: {}", member.getId(),
				e.getMessage(), e);
			throw e;
		}

		log.info("Fairy tale with ID: {} set to invisible for member ID: {}", fairyTaleId, member.getId());
	}

	private Member getMemberFromUserDetails(CustomUserDetails customUserDetails) {
		try {
			return memberRepository.findById(customUserDetails.getMemberId())
				.orElseThrow(() -> {
					log.error("Member not found with ID: {}", customUserDetails.getMemberId());
					return new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
				});
		} catch (NotFoundException e) {
			log.error("Error occurred while fetching member with ID {}: {}", customUserDetails.getMemberId(),
				e.getMessage(), e);
			throw e;
		}
	}
}

