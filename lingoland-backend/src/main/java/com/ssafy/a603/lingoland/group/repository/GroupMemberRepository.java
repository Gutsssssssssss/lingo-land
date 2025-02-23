package com.ssafy.a603.lingoland.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.a603.lingoland.group.entity.GroupMember;
import com.ssafy.a603.lingoland.group.entity.GroupMemberId;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
    boolean existsByGroupIdAndMemberId(Integer groupId, Integer memberId);
}
