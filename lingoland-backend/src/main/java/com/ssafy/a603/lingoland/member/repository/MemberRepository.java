package com.ssafy.a603.lingoland.member.repository;

import com.ssafy.a603.lingoland.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface MemberRepository extends JpaRepository<Member, Integer> {
    boolean existsByLoginId(String loginId);

    Optional<Member> findByLoginId(String loginId);
}
