package com.ssafy.a603.lingoland.room.entity;

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
public class RoomId implements Serializable {
	private String sessionId;
	private Integer memberId;

	@Builder
	public RoomId(String sessionId, Integer memberId) {
		this.sessionId = sessionId;
		this.memberId = memberId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		RoomId roomId = (RoomId)o;
		return sessionId.equals(roomId.sessionId) && memberId.equals(roomId.memberId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sessionId, memberId);
	}

	@Override
	public String toString() {
		return "RoomId{" +
			"sessionId='" + sessionId + '\'' +
			", memberId=" + memberId +
			'}';
	}
}
