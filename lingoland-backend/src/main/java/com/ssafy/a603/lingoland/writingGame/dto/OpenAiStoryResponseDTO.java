package com.ssafy.a603.lingoland.writingGame.dto;

public record OpenAiStoryResponseDTO(String medium, String character, String environment, String lighting, String color,
									 String mood, String composition) {
	@Override
	public String toString() {
		return "{" +
			"medium='" + medium + '\'' +
			", character='" + character + '\'' +
			", environment='" + environment + '\'' +
			", lighting='" + lighting + '\'' +
			", color='" + color + '\'' +
			", mood='" + mood + '\'' +
			", composition='" + composition + '\'' +
			'}';
	}
}
