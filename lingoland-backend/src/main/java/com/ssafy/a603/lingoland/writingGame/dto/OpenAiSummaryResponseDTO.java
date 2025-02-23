package com.ssafy.a603.lingoland.writingGame.dto;

public record OpenAiSummaryResponseDTO(String title, String summary, String medium, String character,
									   String environment, String lighting, String color,
									   String mood, String composition) {

	public OllamaStoryResponseDTO toPrompt() {
		return new OllamaStoryResponseDTO(medium, character, environment, lighting, color, mood, composition);
	}

	@Override
	public String toString() {
		return "{" +
			"title='" + title + '\'' +
			", summary='" + summary + '\'' +
			", medium='" + medium + '\'' +
			", character='" + character + '\'' +
			", environment='" + environment + '\'' +
			", lighting='" + lighting + '\'' +
			", color='" + color + '\'' +
			", mood='" + mood + '\'' +
			", composition='" + composition + '\'' +
			'}';
	}
}
