<script setup>
import { useProblemStore } from "@/stores/problem";
import { defineProps, ref } from "vue";
import GenericButton from "../common/GenericButton.vue";

const emit = defineEmits(["clickEvent"]);

const props = defineProps({
    incorrect: Object,
    memberId: String,
});

const problemStore = useProblemStore();

const isStudy = ref(false);
const answerColor = ref("");
const myAnswerColor = ref("");

function studyProblem() {
    // 학습하기 버튼을 누르면 답변에 색칠
    // 정답은 초록색, 내가 선택한 답은 빨간색
    // 문제 해설 보이기
    isStudy.value = !isStudy.value;

    if (isStudy.value) {
        answerColor.value = "#00C853";
        myAnswerColor.value = "#FF1744";
    }
}

function completeProblem(problemId) {
    const result = problemStore.delectMyWrongProblem(problemId);

    if (result) {
        emit("clickEvent");
    }
}
</script>

<template>
    <v-container class="border-md">
        <v-row class="d-flex flex-column">
            <v-col>
                <div class="text-h4">{{ incorrect.problem }}</div>
            </v-col>

            <template v-for="(choice, index) in incorrect.choices" :key="index">
                <v-col>
                    <div
                        class="text-h5"
                        :style="{
                            color:
                                isStudy && choice.num === incorrect.answer
                                    ? answerColor
                                    : isStudy &&
                                      choice.num === incorrect.submittedAnswer
                                    ? myAnswerColor
                                    : '',
                        }"
                    >
                        {{ choice.num }}. {{ choice.text }}
                    </div>
                </v-col>
            </template>

            <template v-if="isStudy">
                <v-col>
                    <div class="text-h6">문제 해설</div>
                    <div class="text-h6">
                        {{ incorrect.explanation }}
                    </div>
                </v-col>
            </template>

            <v-col v-if="!props.memberId">
                <div class="d-flex justify-end">
                    <GenericButton
                        :style="{
                            backgroundColor: '#4C4637',
                            color: 'white',
                        }"
                        class="mr-5"
                        data="학습하기"
                        @click-event="studyProblem"
                    />

                    <GenericButton
                        :style="{
                            backgroundColor: '#ECEFF1',
                            color: 'black',
                        }"
                        data="학습완료"
                        @click-event="completeProblem(incorrect.problemId)"
                    />
                </div>
            </v-col>
        </v-row>
    </v-container>
</template>

<style scoped>
.border-md {
    background-color: #fff1c2;
    border-radius: 8px;
    margin: 8px 0px;
}
</style>
