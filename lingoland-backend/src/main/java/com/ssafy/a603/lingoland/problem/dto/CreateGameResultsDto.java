package com.ssafy.a603.lingoland.problem.dto;

import lombok.Getter;


import java.util.List;

@Getter
public class CreateGameResultsDto {

    private List<ProblemDto> problemList;
    private Integer coinCount;
}
