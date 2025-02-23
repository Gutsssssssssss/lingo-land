package com.ssafy.a603.lingoland.member.dto;

import lombok.Builder;

@Builder
public record GetMemberInfoDto(
        String nickname,
        String profileImage,
        long maxExperiencePoint,
        long experiencePoint,
        String rank) {
}
