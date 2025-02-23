package com.ssafy.a603.lingoland.fairyTale.repository;

import static com.ssafy.a603.lingoland.fairyTale.entity.QFairyTale.fairyTale;
import static com.ssafy.a603.lingoland.fairyTale.entity.QFairyTaleMember.fairyTaleMember;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.a603.lingoland.fairyTale.dto.FairyTaleListResponseDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FairyTaleCustomRepositoryImpl implements FairyTaleCustomRepository {
	private final JPAQueryFactory queryFactory;

	public List<FairyTaleListResponseDTO> findAllFairyTalesByMemberId(int memberId) {
		return queryFactory.select(
				Projections.constructor(FairyTaleListResponseDTO.class,
					fairyTale.id,
					fairyTale.title,
					fairyTale.cover,
					fairyTale.summary
				))
			.from(fairyTale)
			.join(fairyTaleMember).on(fairyTaleMember.fairyTale.id.eq(fairyTale.id))
			.where(
				isEnd(),
				isVisible(),
				targetGroup(memberId)
			).fetch();
	}

	private BooleanExpression isEnd() {
		return fairyTale.isComplete.ne(0);
	}

	private BooleanExpression isVisible() {
		return fairyTaleMember.isVisible.eq(true);
	}

	private BooleanExpression targetGroup(int memberId) {
		return fairyTaleMember.member.id.eq(memberId);
	}
}