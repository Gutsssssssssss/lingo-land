package com.ssafy.a603.lingoland.problem.entity;

import com.ssafy.a603.lingoland.global.entity.BaseTimeEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Where(clause = "is_deleted = false")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProblemMember extends BaseTimeEntity {

    @EmbeddedId
    private ProblemMemberId id;

    private int submittedAnswer;

    private boolean isCorrect;

    private boolean isDeleted = false;

    private int inCorrectCount;

    @Builder
    public ProblemMember(int submittedAnswer, ProblemMemberId id) {
        this.submittedAnswer = submittedAnswer;
        this.id = id;
    }

    public void updateCorrectAnswer() {
        this.isCorrect = true;
    }

    public void updateInCorrectAnswer() {
        this.isCorrect = false;
        this.inCorrectCount++;
        if(this.isDeleted == true) {
            this.isDeleted = false;
        }
    }

}
