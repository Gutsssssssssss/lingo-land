package com.ssafy.a603.lingoland.fairyTale.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssafy.a603.lingoland.fairyTale.dto.UpdateFairyTaleRequestDTO;
import com.ssafy.a603.lingoland.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FairyTale extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fairy_tale_seq")
	@SequenceGenerator(name = "fairy_tale_seq", sequenceName = "fairy_tale_id_seq", allocationSize = 1)
	private Integer id;

	@Column(name = "title")
	private String title;

	@Column(name = "cover")
	private String cover;

	@Column(name = "summary", length = 2000)
	private String summary;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private List<Story> content;

	@OneToMany(mappedBy = "fairyTale")
	@JsonIgnore
	private List<FairyTaleMember> fairyTaleMembers;

	private Integer isComplete;

	@Builder
	public FairyTale(String title, String cover, String summary, List<Story> content) {
		this.title = title;
		this.cover = cover;
		this.summary = summary;
		this.content = content;
		this.fairyTaleMembers = new ArrayList<>();
		this.isComplete = 0;
	}

	public void complete() {
		this.isComplete = 2;
	}

	public void inComplete() {
		this.isComplete = 1;
	}

	public void addContent(FairyTale.Story story) {
		this.content.add(story);
	}

	public void update(UpdateFairyTaleRequestDTO request) {
		if (request.title() != null && !request.title().isBlank())
			this.title = request.title();
		if (request.summary() != null && !request.summary().isBlank())
			this.summary = request.summary();
	}

	public void completeBefore(String cover, String summary) {
		this.cover = cover;
		this.summary = summary;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Story {
		private String illustration;
		private String story;

		@Builder
		public Story(@JsonProperty("illustration") String illustration,
			@JsonProperty("story") String story) {
			this.illustration = illustration;
			this.story = story;
		}
	}
}
