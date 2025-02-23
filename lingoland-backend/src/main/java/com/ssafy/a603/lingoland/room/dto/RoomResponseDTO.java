package com.ssafy.a603.lingoland.room.dto;

import lombok.Builder;

@Builder
public record RoomResponseDTO(String code, Integer memberCount) {
}
