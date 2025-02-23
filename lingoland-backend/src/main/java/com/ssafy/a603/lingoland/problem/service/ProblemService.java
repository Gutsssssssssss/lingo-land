package com.ssafy.a603.lingoland.problem.service;

import com.ssafy.a603.lingoland.member.security.CustomUserDetails;
import com.ssafy.a603.lingoland.problem.dto.CreateGameResultsDto;
import com.ssafy.a603.lingoland.problem.dto.GetProblemDto;
import com.ssafy.a603.lingoland.problem.dto.GetWrongProblemsDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProblemService {

    void createGameResults(CreateGameResultsDto createGameResultsDto, CustomUserDetails customUserDetails);

    List<GetWrongProblemsDto> getWrongProblems(CustomUserDetails customUserDetails);

    List<GetWrongProblemsDto> getWrongProblemsByGroupLeader(Integer groupId, String memberId, CustomUserDetails customUserDetails);

    void deleteWrongProblems(Integer problemId, CustomUserDetails customUserDetails);


    List<GetProblemDto> getProblems();
}
