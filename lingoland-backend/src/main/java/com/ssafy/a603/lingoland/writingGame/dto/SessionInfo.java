package com.ssafy.a603.lingoland.writingGame.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionInfo {
	private int maxTurn;
	private Set<String> participants;

	public SessionInfo(int maxTurn) {
		this.maxTurn = maxTurn;
		this.participants = new HashSet<>();
	}

	public void addParticipant(String participant) {
		this.participants.add(participant);
	}
}
