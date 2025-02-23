package com.ssafy.a603.lingoland.group.dto;

import lombok.Builder;

@Builder
public record CreateGroupDTO(String name, Integer password, String description) {
}
