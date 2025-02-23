package com.ssafy.a603.lingoland.fairyTale.service;

import java.util.List;

import com.ssafy.a603.lingoland.fairyTale.dto.FairyTaleListResponseDTO;
import com.ssafy.a603.lingoland.fairyTale.dto.UpdateFairyTaleRequestDTO;
import com.ssafy.a603.lingoland.fairyTale.entity.FairyTale;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;

public interface FairyTaleService {
	FairyTale createFairyTale(String title, String cover, String summary, List<FairyTale.Story> content,
		List<String> writers);

	List<FairyTaleListResponseDTO> findFairyTaleListByLoginId(String loginId);

	List<FairyTaleListResponseDTO> findFairyTaleListByLoginId(CustomUserDetails customUserDetails);

	FairyTale findFairyTaleById(Integer fairyTaleId);

	void updateFairyTale(UpdateFairyTaleRequestDTO updateFairyTaleRequestDTO);

	void fairyTaleInvisible(Integer fairyTaleId, CustomUserDetails customUserDetails);
}
