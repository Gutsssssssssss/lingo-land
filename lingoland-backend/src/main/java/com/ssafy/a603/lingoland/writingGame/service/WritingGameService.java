package com.ssafy.a603.lingoland.writingGame.service;

import com.ssafy.a603.lingoland.member.security.CustomUserDetails;
import com.ssafy.a603.lingoland.writingGame.dto.DrawingRequestDTO;
import com.ssafy.a603.lingoland.writingGame.dto.SubmitStoryResponseDTO;
import com.ssafy.a603.lingoland.writingGame.dto.WritingGameStartRequestDTO;

public interface WritingGameService {
	void start(String sessionId, WritingGameStartRequestDTO request);

	void setTitle(String sessionId, CustomUserDetails customUserDetails, String title);

	SubmitStoryResponseDTO submitStory(String sessionId, DrawingRequestDTO dto, CustomUserDetails customUserDetails);

	Boolean exit(String sessionId, String exitLoginId, Integer order);
}
