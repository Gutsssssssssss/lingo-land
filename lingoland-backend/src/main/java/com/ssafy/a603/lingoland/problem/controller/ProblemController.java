package com.ssafy.a603.lingoland.problem.controller;

import com.ssafy.a603.lingoland.member.security.CurrentUser;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;
import com.ssafy.a603.lingoland.problem.dto.CreateGameResultsDto;
import com.ssafy.a603.lingoland.problem.dto.GetProblemDto;
import com.ssafy.a603.lingoland.problem.dto.GetWrongProblemsDto;
import com.ssafy.a603.lingoland.problem.service.ProblemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("api/v1/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @PostMapping("/save-results")
    public ResponseEntity<?> createGameResults(@Valid @RequestBody CreateGameResultsDto createGameResultsDto,
                                               @CurrentUser CustomUserDetails customUserDetails) {
        problemService.createGameResults(createGameResultsDto, customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // should be revised to find random problem of specific kind.

    @GetMapping("/wrong-problems")
    public ResponseEntity<?> getWrongProblems(@CurrentUser CustomUserDetails customUserDetails) {
        List<GetWrongProblemsDto> getWrongProblemsDtos = problemService.getWrongProblems(customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).body(getWrongProblemsDtos);
    }

    @GetMapping("/wrong-problems/{groupId}/{memberId}")
    public ResponseEntity<?> getWrongProblemsByGroupLeader(@PathVariable(value = "groupId") Integer groupId,
                                                           @PathVariable(value = "memberId") String memberId,
                                                           @CurrentUser CustomUserDetails customUserDetails) {
        List<GetWrongProblemsDto> getWrongProblemsDtos = problemService.getWrongProblemsByGroupLeader(groupId, memberId, customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).body(getWrongProblemsDtos);
    }

    @GetMapping
    public ResponseEntity<?> getProblems() {
        List<GetProblemDto> getProblemDtos = problemService.getProblems();
        return ResponseEntity.status(HttpStatus.OK).body(getProblemDtos);
    }

    @DeleteMapping("/wrong-problems/{problemId}")
    public ResponseEntity<?> deleteWrongProblems(@PathVariable(value = "problemId") Integer problemId,
            @CurrentUser CustomUserDetails customUserDetails) {
        problemService.deleteWrongProblems(problemId, customUserDetails);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
