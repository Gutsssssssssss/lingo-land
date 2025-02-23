package com.ssafy.a603.lingoland.fairyTale.dto;

import lombok.Builder;

@Builder
public record FairyTaleListResponseDTO(int id, String title, String cover, String summary) {
}
