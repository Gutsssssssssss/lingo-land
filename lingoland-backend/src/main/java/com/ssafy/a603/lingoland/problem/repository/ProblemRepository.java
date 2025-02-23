package com.ssafy.a603.lingoland.problem.repository;

import com.ssafy.a603.lingoland.problem.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {

    @Query(value = "SELECT * FROM problem ORDER BY RANDOM() LIMIT 10", nativeQuery = true)
    List<Problem> findRandomProblems();
}
