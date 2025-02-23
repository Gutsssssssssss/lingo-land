package com.ssafy.a603.lingoland.fairyTale.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FairyTaleMemberId implements Serializable {
	private Integer fairyTaleId;
	private Integer memberId;

	@Builder
	protected FairyTaleMemberId(Integer fairyTaleId, Integer memberId) {
		this.fairyTaleId = fairyTaleId;
		this.memberId = memberId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		FairyTaleMemberId that = (FairyTaleMemberId)o;
		return Objects.equals(fairyTaleId, that.fairyTaleId) && Objects.equals(memberId, that.memberId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fairyTaleId, memberId);
	}
}
