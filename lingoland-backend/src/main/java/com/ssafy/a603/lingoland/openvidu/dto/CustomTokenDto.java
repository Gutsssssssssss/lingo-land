package com.ssafy.a603.lingoland.openvidu.dto;

import lombok.Builder;

@Builder
public record CustomTokenDto(String sessionId, String token) {
}
