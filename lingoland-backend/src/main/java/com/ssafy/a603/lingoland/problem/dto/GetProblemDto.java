package com.ssafy.a603.lingoland.problem.dto;

import com.ssafy.a603.lingoland.problem.entity.Problem;
import lombok.Builder;

import java.util.List;

@Builder
public record GetProblemDto(
        Integer problemId,
        String problem,
        List<Problem.Detail.Choice> choices,
        int answer,
        String explanation
) {
}
