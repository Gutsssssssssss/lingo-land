package com.ssafy.a603.lingoland.problem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProblemDto {

    @NotBlank
    private int problemId;

    @NotBlank
    private int answer;
}
