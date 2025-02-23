package com.ssafy.a603.lingoland.room.entity;

import java.time.LocalDateTime;

import com.ssafy.a603.lingoland.fairyTale.entity.FairyTale;
import com.ssafy.a603.lingoland.global.entity.BaseTimeEntity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Room extends BaseTimeEntity {
	@EmbeddedId
	private RoomId id;

	@OneToOne
	private FairyTale fairyTale;

	private Boolean isDeleted;

	private LocalDateTime deletedAt;

	@Builder
	public Room(RoomId id, FairyTale fairyTale) {
		this.id = id;
		this.fairyTale = fairyTale;
		this.isDeleted = false;
	}

	public void delete() {
		this.isDeleted = true;
		this.deletedAt = LocalDateTime.now();
	}
}
