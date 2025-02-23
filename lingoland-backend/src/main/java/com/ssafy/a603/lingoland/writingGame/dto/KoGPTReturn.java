package com.ssafy.a603.lingoland.writingGame.dto;

public record KoGPTReturn(String id, Generation[] generations, Usage usage) {
	public record Generation(String text, Integer tokens) {
	}

	public record Usage(Integer prompt_tokens, Integer generated_tokens, Integer total_tokens) {
	}
}
