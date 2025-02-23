package com.ssafy.a603.lingoland.problem.service;

import com.ssafy.a603.lingoland.global.error.entity.ErrorCode;
import com.ssafy.a603.lingoland.global.error.exception.ForbiddenException;
import com.ssafy.a603.lingoland.group.entity.Group;
import com.ssafy.a603.lingoland.group.repository.GroupMemberRepository;
import com.ssafy.a603.lingoland.group.repository.GroupRepository;
import com.ssafy.a603.lingoland.member.entity.Member;
import com.ssafy.a603.lingoland.member.repository.MemberRepository;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;
import com.ssafy.a603.lingoland.problem.dto.CreateGameResultsDto;
import com.ssafy.a603.lingoland.problem.dto.GetProblemDto;
import com.ssafy.a603.lingoland.problem.dto.GetWrongProblemsDto;
import com.ssafy.a603.lingoland.problem.entity.Problem;
import com.ssafy.a603.lingoland.problem.entity.ProblemMember;
import com.ssafy.a603.lingoland.problem.entity.ProblemMemberId;
import com.ssafy.a603.lingoland.problem.repository.ProblemMemberRepository;
import com.ssafy.a603.lingoland.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemMemberRepository problemMemberRepository;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    @Override
    public void createGameResults(CreateGameResultsDto createGameResultsDto, CustomUserDetails customUserDetails) {
        Member member = getMember(customUserDetails);
        AtomicInteger correctAnswerCount = new AtomicInteger(0);

        createGameResultsDto.getProblemList().forEach(problemDto -> {
            Problem problem = problemRepository.findById(problemDto.getProblemId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid problem id: " + problemDto.getProblemId()));

            Optional<ProblemMember> optionalProblemMember = problemMemberRepository.findByProblemIdAndMemberIdIncludingDeleted(problem.getId(), member.getId());
            ProblemMember problemMember;
            if(optionalProblemMember.isPresent()) {
                problemMember = optionalProblemMember.get();
            } else {
                ProblemMemberId problemMemberId = new ProblemMemberId(member, problem);
                problemMember = ProblemMember.builder()
                        .id(problemMemberId)
                        .submittedAnswer(problemDto.getAnswer())
                        .build();
            }

            boolean isCorrect = updateProblemMemberAndProblem(problem, problemMember, problemDto.getAnswer());

            if(isCorrect) {
                correctAnswerCount.incrementAndGet();
            }

            problemMemberRepository.save(problemMember);
        });
        int problemPoint = correctAnswerCount.get() * 5;
        int coinPoint = createGameResultsDto.getCoinCount() / 10;
        member.updateExperiencePoint(problemPoint + coinPoint);
    }

    @Override
    public List<GetProblemDto> getProblems() {
        List<Problem> problems = problemRepository.findRandomProblems();
        return problems.stream()
                .map(Problem::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetWrongProblemsDto> getWrongProblems(CustomUserDetails customUserDetails) {
        Integer memberId = customUserDetails.getMemberId();
        List<ProblemMember> problemMembers = problemMemberRepository.findByMemberIdAndIsCorrectFalse(memberId);

        return problemMembers.stream()
                .map(this::mapToGetWrongProblemsDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetWrongProblemsDto> getWrongProblemsByGroupLeader(Integer groupId, String memberId, CustomUserDetails customUserDetails) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Group"));
        Integer memberIdByRequest = customUserDetails.getMemberId();
        int memberIdByLoginId = getMember(memberId).getId();

        if(group.getLeader().getId() != memberIdByRequest) {
            throw new ForbiddenException(ErrorCode.GROUP_NOT_LEADER);
        }

        if(!groupMemberRepository.existsByGroupIdAndMemberId(groupId, memberIdByLoginId)) {
            throw new IllegalArgumentException("Invalid Member");
        }

        List<ProblemMember> problemMembers = problemMemberRepository.findByMemberIdAndIsCorrectFalse(memberIdByLoginId);

        return problemMembers.stream()
                .map(this::mapToGetWrongProblemsDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteWrongProblems(Integer problemId, CustomUserDetails customUserDetails) {
        problemMemberRepository.deleteByMemberIdAndProblemId(problemId, customUserDetails.getMemberId());
    }

    private boolean updateProblemMemberAndProblem(Problem problem, ProblemMember problemMember, int answer) {
        if (answer == problem.getDetail().getAnswer()) {
            problemMember.updateCorrectAnswer();
            problem.updateCorrectAnswerCount();
            return true;
        } else {
            problemMember.updateInCorrectAnswer();
            problem.updateInCorrectAnswerCount();
            return false;
        }
    }

    private GetWrongProblemsDto mapToGetWrongProblemsDto(ProblemMember problemMember) {
        Integer problemId = problemMember.getId().getProblem().getId();
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Problem"));

        return GetWrongProblemsDto.builder()
                .problemId(problemId)
                .problem(problem.getDetail().getProblem())
                .choices(problem.getDetail().getChoices())
                .answer(problem.getDetail().getAnswer())
                .explanation(problem.getDetail().getExplanation())
                .submittedAnswer(problemMember.getSubmittedAnswer())
                .build();
    }

    private Member getMember(CustomUserDetails customUserDetails) {
        return memberRepository.findById(customUserDetails.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member"));
    }

    private Member getMember(String memberId) {
        return memberRepository.findByLoginId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member"));
    }
}
