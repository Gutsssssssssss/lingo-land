package com.ssafy.a603.lingoland.room.service;

import java.util.List;

import com.ssafy.a603.lingoland.fairyTale.entity.FairyTale;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;

public interface RoomService {
	// 룸 서비스에서 기대하는 것, with writing game service

	// redis에 저장하던 글에 대한 정보는 여기에 저장해야 한다.

	void create(String sessionId, CustomUserDetails customUserDetails, String title);

	void fairytaleStoryAdd(String sessionId, String starterLoginId, CustomUserDetails contributor,
		FairyTale.Story story);

	void fairytaleComplete(String sessionId, String starterLoginId, String cover, String summary);

	void fairytaleInComplete(String sessionId, String starterLoginId);

	void endRooms(String sessionId);

	void endRoom(String sessionId, String starterLoginId);

	FairyTale findFairyTale(String sessionId, String loginId);

	List<FairyTale> findFairyTalesInSession(String sessionId);
}
