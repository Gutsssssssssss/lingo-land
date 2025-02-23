package com.ssafy.a603.lingoland.fairyTale.entity;

import java.time.LocalDateTime;

import com.ssafy.a603.lingoland.global.entity.BaseEntity;
import com.ssafy.a603.lingoland.member.entity.Member;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FairyTaleMember extends BaseEntity {

	@EmbeddedId
	private FairyTaleMemberId id;

	private boolean isVisible;
	private LocalDateTime invisibleAt;

	@MapsId("fairyTaleId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fairy_tale_id")
	private FairyTale fairyTale;

	@MapsId("memberId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	public FairyTaleMember(FairyTale fairyTale, Member member) {
		this.id = FairyTaleMemberId.builder()
			.fairyTaleId(fairyTale.getId())
			.memberId(member.getId())
			.build();
		this.fairyTale = fairyTale;
		this.member = member;
		this.isVisible = true;
	}

	public void invisible() {
		this.isVisible = false;
		this.invisibleAt = LocalDateTime.now();
	}

	public void visible() {
		this.isVisible = true;
		this.invisibleAt = null;
	}
}
