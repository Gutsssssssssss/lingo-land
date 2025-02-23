package com.ssafy.a603.lingoland.fairyTale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ssafy.a603.lingoland.fairyTale.entity.FairyTaleMember;
import com.ssafy.a603.lingoland.fairyTale.entity.FairyTaleMemberId;

@Repository
public interface FairyTaleMemberRepository extends JpaRepository<FairyTaleMember, FairyTaleMemberId> {
	boolean existsByFairyTaleIdAndMemberId(Integer fairyTaleId, Integer memberId);

	@Query("SELECT f.isVisible FROM FairyTaleMember f WHERE f.id = :id")
	boolean findIsVisibleById(@Param("id") FairyTaleMemberId id);
}
