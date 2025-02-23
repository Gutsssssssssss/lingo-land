package com.ssafy.a603.lingoland.group.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ssafy.a603.lingoland.global.entity.BaseEntity;
import com.ssafy.a603.lingoland.group.dto.UpdateGroupDTO;
import com.ssafy.a603.lingoland.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Entity
@Table(name = "\"group\"")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_seq")
	@SequenceGenerator(name = "group_seq", sequenceName = "group_id_seq", allocationSize = 1)
	private Integer id;

	@Column(name = "name", nullable = false, length = 20)
	private String name;

	@Column(name = "password", nullable = false)
	private Integer password;

	@Column(name = "description", length = 200)
	private String description;

	@Column(name = "member_count", nullable = false, columnDefinition = "integer default 1")
	private int memberCount = 1;

	@Column(name = "group_image", length = 256)
	private String groupImage;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
	private boolean isDeleted = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "leader_id")
	private Member leader;

	@OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<GroupMember> groupMembers = new ArrayList<>();

	@Builder
	protected Group(String name, Integer password, String description, String groupImage, Member leader) {
		this.name = name;
		this.password = password;
		this.description = description;
		this.groupImage = groupImage;
		this.leader = leader;
		this.memberCount = 0;
		this.isDeleted = false;
	}

	public void updateGroup(UpdateGroupDTO request) {
		if (request.name() != null && !request.name().isBlank()) {
			this.name = request.name();
		}
		if (request.password() != null) {
			this.password = request.password();
		}
		if (request.description() != null && !request.description().isBlank()) {
			this.description = request.description();
		}
	}

	public void delete() {
		this.isDeleted = true;
		this.deletedAt = LocalDateTime.now();
	}

	public void setGroupImagePath(String path) {
		this.groupImage = path;
	}

	public void join() {
		this.memberCount++;
	}

	public void quit() {
		this.memberCount--;
	}
}
