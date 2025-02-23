package com.ssafy.a603.lingoland.group.dto;

import lombok.Builder;

@Builder
public record UpdateGroupDTO(Integer id, String name, Integer password, String description) {
}
