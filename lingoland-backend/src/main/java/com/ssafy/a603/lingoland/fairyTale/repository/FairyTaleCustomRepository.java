package com.ssafy.a603.lingoland.fairyTale.repository;

import java.util.List;

import com.ssafy.a603.lingoland.fairyTale.dto.FairyTaleListResponseDTO;

public interface FairyTaleCustomRepository {
	public List<FairyTaleListResponseDTO> findAllFairyTalesByMemberId(int memberId);
}
