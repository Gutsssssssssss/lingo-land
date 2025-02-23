package com.ssafy.a603.lingolandbatch.problem;


import com.ssafy.a603.lingolandbatch.problem.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
    @AllArgsConstructor
    public static class Detail {
        private String problem;
        private List<Choice> choices;
        private int answer;
        private String explanation;

        @Getter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        @AllArgsConstructor
        public static class Choice {
            private int num;
            private String text;
        }
    }
}
