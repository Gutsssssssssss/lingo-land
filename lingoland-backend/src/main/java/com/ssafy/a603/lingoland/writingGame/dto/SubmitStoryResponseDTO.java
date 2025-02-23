package com.ssafy.a603.lingoland.writingGame.dto;

import java.util.List;

import com.ssafy.a603.lingoland.fairyTale.entity.FairyTale;

import lombok.Builder;

@Builder
public record SubmitStoryResponseDTO(List<FairyTale> fairyTales, Boolean goNext) {
}
