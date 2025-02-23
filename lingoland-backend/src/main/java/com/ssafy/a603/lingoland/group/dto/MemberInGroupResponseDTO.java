package com.ssafy.a603.lingoland.group.dto;

public record MemberInGroupResponseDTO(String loginId, String nickname, String profileImage, String description,
									   boolean isLeader) { //신분, 경험치
}
