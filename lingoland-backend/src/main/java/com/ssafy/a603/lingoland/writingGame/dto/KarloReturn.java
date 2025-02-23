package com.ssafy.a603.lingoland.writingGame.dto;

public record KarloReturn(String id, String model_version, Image[] images) {
	public record Image(String id, Long seed, String image) {
	}
}
