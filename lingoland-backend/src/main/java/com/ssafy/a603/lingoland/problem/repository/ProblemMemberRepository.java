package com.ssafy.a603.lingoland.problem.repository;

import com.ssafy.a603.lingoland.problem.entity.ProblemMember;
import com.ssafy.a603.lingoland.problem.entity.ProblemMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProblemMemberRepository extends JpaRepository<ProblemMember, ProblemMemberId> {

    @Query("SELECT pm FROM ProblemMember pm WHERE pm.id.member.id = :memberId AND pm.isCorrect = false")
    List<ProblemMember> findByMemberIdAndIsCorrectFalse(@Param("memberId") Integer memberId);

    @Modifying
    @Transactional
    @Query("UPDATE ProblemMember pm SET pm.isDeleted = true WHERE pm.id.member.id = :memberId AND pm.id.problem.id = :problemId")
    void deleteByMemberIdAndProblemId(@Param("problemId") Integer problemId, @Param("memberId") Integer memberId);

    @Query(value = "SELECT * FROM problem_member pm WHERE pm.problem_id = :problemId AND pm.member_id = :memberId", nativeQuery = true)
    Optional<ProblemMember> findByProblemIdAndMemberIdIncludingDeleted(@Param("problemId") Integer problemId, @Param("memberId") Integer memberId);
}
