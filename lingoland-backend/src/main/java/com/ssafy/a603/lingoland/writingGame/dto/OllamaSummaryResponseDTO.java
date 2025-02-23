package com.ssafy.a603.lingoland.writingGame.dto;

import lombok.Builder;

@Builder
public record OllamaSummaryResponseDTO(String title, String summary, OllamaStoryResponseDTO content) {
	@Override
	public String toString() {
		return "{" +
			"title='" + title + '\'' +
			", summary='" + summary + '\'' +
			", content='" + content.toString() + '\'' +
			'}';
	}
}
