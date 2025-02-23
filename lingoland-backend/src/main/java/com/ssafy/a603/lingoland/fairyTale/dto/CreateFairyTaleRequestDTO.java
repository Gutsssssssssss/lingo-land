package com.ssafy.a603.lingoland.fairyTale.dto;

import java.util.List;

import com.ssafy.a603.lingoland.fairyTale.entity.FairyTale;

public record CreateFairyTaleRequestDTO(String title, String cover, String summary, List<FairyTale.Story> content,
										List<String> writers) {
}
