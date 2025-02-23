package com.ssafy.a603.lingoland.group.dto;

public record GroupInfoResponseDTO(Integer id, String name, String description, Integer memberCount,
								   String leaderNickname,
								   String groupImage) {
}
