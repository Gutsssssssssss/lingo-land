package com.ssafy.a603.lingoland.group.repository;

import static com.ssafy.a603.lingoland.group.entity.QGroup.group;
import static com.ssafy.a603.lingoland.group.entity.QGroupMember.groupMember;
import static com.ssafy.a603.lingoland.member.entity.QMember.member;

import java.util.List;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.a603.lingoland.group.dto.GroupInfoResponseDTO;
import com.ssafy.a603.lingoland.group.dto.MemberInGroupResponseDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GroupCustomRepositoryImpl implements GroupCustomRepository {
	private final JPAQueryFactory queryFactory;

	// 그룹ID 가 같은 멤버 모두 출력 (그룹원 목록 출력)
	public List<MemberInGroupResponseDTO> findAllMembersInGroup(Integer groupId, String keyword) {
		List<MemberInGroupResponseDTO> members = queryFactory.select(
				Projections.constructor(MemberInGroupResponseDTO.class,
					member.loginId,
					member.nickname,
					member.profileImage,
					groupMember.description,
					member.id.eq(group.leader.id).as("isLeader")
				))
			.from(member)
			.join(groupMember).on(member.id.eq(groupMember.member.id)).fetchJoin()
			.join(group).on(groupMember.group.id.eq(group.id)).fetchJoin()
			.where(
				applySoftDelete(),
				targetGroup(groupId),
				containsNickname(keyword)
			)
			.fetch();
		return members;
	}

	//member가 속한 모든 그룹 목록 출력
	public List<GroupInfoResponseDTO> findGroupsByMemberId(Integer memberId, String keyword, boolean includeMember) {
		return queryFactory.select(groupInfoProjection())
			.distinct()
			.from(groupMember)
			.join(group).on(group.id.eq(groupMember.group.id)).fetchJoin()
			.join(member).on(member.id.eq(groupMember.member.id)).fetchJoin()
			.where(
				applySoftDelete(),
				includeMember ? includeMember(memberId) : excludeMember(memberId),
				containsGroupname(keyword)
			)
			.fetch();
	}

	@Override
	public GroupInfoResponseDTO findGroupInfoById(Integer groupId) {
		return queryFactory.select(groupInfoProjection())
			.from(groupMember)
			.join(group).on(group.id.eq(groupMember.group.id)).fetchJoin()
			.join(member).on(member.id.eq(groupMember.member.id)).fetchJoin()
			.where(
				applySoftDelete(),
				targetGroup(groupId),
				onlyLeader()
			)
			.fetchOne();
	}

	private ConstructorExpression<GroupInfoResponseDTO> groupInfoProjection() {
		return Projections.constructor(GroupInfoResponseDTO.class,
			group.id,
			group.name,
			group.description,
			group.memberCount,
			group.leader.nickname,
			group.groupImage
		);
	}

	private BooleanExpression applySoftDelete() {
		return groupMember.isDeleted.eq(false);
	}

	private BooleanExpression includeMember(Integer memberId) {
		return memberId == null ? null : member.id.eq(memberId);
	}

	private BooleanExpression excludeMember(Integer memberId) {
		// 멤버가 속한 그룹 ID 조회
		List<Integer> includedGroupIds = queryFactory
			.select(groupMember.group.id)
			.from(groupMember)
			.where(groupMember.member.id.eq(memberId))
			.fetch();

		// 제외할 그룹 ID를 조건에 추가
		return group.id.notIn(includedGroupIds);
	}

	private BooleanExpression targetGroup(Integer groupId) {
		return groupId == null ? null : group.id.eq(groupId);
	}

	private BooleanExpression onlyLeader() {
		return group.leader.id.eq(member.id);
	}

	private BooleanExpression containsNickname(String keyword) {
		return StringUtils.isNullOrEmpty(keyword) ? null : member.nickname.containsIgnoreCase(keyword);
	}

	private BooleanExpression containsGroupname(String keyword) {
		return StringUtils.isNullOrEmpty(keyword) ? null : group.name.containsIgnoreCase(keyword);
	}
}
