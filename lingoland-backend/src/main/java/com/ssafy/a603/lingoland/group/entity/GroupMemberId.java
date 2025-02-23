package com.ssafy.a603.lingoland.group.entity;

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
public class GroupMemberId implements Serializable {

	private Integer groupId;
	private Integer memberId;

	@Builder
	protected GroupMemberId(Integer groupId, Integer memberId) {
		this.groupId = groupId;
		this.memberId = memberId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		GroupMemberId that = (GroupMemberId)o;
		return Objects.equals(groupId, that.groupId) && Objects.equals(memberId, that.memberId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(groupId, memberId);
	}
}