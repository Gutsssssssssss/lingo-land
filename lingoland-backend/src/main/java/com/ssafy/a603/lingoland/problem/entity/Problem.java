package com.ssafy.a603.lingoland.problem.entity;

import com.ssafy.a603.lingoland.global.entity.BaseTimeEntity;
import com.ssafy.a603.lingoland.problem.dto.GetProblemDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Problem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROBLEM_SEQ_GENERATOR")
    @SequenceGenerator(name = "PROBLEM_SEQ_GENERATOR", sequenceName = "fairy_tale_id_seq", allocationSize = 1)
    private Integer id;

    private long correctAnswerCount;

    private long incorrectAnswerCount;

    private LocalDateTime deletedAt;

    private boolean isDeleted;

    private String inspector;

    private String creator;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Detail detail;

    public void updateCorrectAnswerCount() {
        this.correctAnswerCount++;
    }

    public void updateInCorrectAnswerCount() {
        this.incorrectAnswerCount++;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Detail {
        private String problem;
        private List<Choice> choices;
        private int answer;
        private String explanation;

        @Getter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public static class Choice {
            private int num;
            private String text;
        }
    }

    public GetProblemDto toDto() {
        return GetProblemDto.builder()
                .problemId(this.id)
                .problem(this.detail.getProblem())
                .choices(this.detail.getChoices())
                .answer(this.detail.getAnswer())
                .explanation(this.detail.getExplanation())
                .build();
    }

}
