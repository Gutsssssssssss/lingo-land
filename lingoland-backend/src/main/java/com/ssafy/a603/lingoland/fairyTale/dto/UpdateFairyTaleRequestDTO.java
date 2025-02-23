package com.ssafy.a603.lingoland.fairyTale.dto;

import lombok.Builder;

@Builder
public record UpdateFairyTaleRequestDTO(Integer id, String title, String summary) {
}
