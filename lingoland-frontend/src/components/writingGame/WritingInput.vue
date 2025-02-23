<script setup>
import { useOpenviduStore } from "@/stores/openvidu";
import { useWritingGameStore } from "@/stores/writingGame";
import { storeToRefs } from "pinia";
import { ref } from "vue";
import { useRoute } from "vue-router";
import GenericInput from "../common/GenericInput.vue";
import GenericInputArea from "../common/GenericInputArea.vue";
import SubmitButton from "../common/SubmitButton.vue";
import CountdownTimer from "./CountdownTimer.vue";

const route = useRoute();

const writingGameStore = useWritingGameStore();
const { turn, isTitle, textareaValue, title } = storeToRefs(writingGameStore);

const openviduStore = useOpenviduStore();
const { session, findCurrentStoryId, findMyLoginId } = openviduStore;

const handleTimesUp = () => {
    let currentStoryId = findCurrentStoryId();

    // 글 제출 시그널 보내기
    session
        .signal({
            type: "writingGame",
            data: JSON.stringify({
                storyId: currentStoryId,
                story: textareaValue.value,
            }),
        })
        .then(() => {
            console.log("******************writingGame submit");
            submit();
            // textarea 초기화하기
            textareaValue.value = "";
        })
        .catch((error) => {
            console.error("***************error sending signal", error);
        });
};

function titleSubmit() {
    writingGameStore.submitTitle(route.params.roomId, {
        title: title.value,
    });
    isTitle.value = false;
}

function submit() {
    // 지금 쓰고 있는 스토리 아이디를 넣어야함
    let myLoginId = findCurrentStoryId();

    const drawingDTO = {
        key: myLoginId, // 로그인 아이디
        story: textareaValue.value,
        order: Number(turn.value) + Number(1),
    };

    console.log("****************제출 DTO", drawingDTO);

    // API 요청 보내기
    writingGameStore
        .submitStory(route.params.roomId, drawingDTO)
        .then((response) => {
            console.log("*******현재 턴", turn.value);
            console.log("********스토리 제출 API 요청 응답", response);
            if (response) {
                session.signal({ type: "turnOver" });
            }
        })
        .catch((error) => {
            console.log("*******************error", error);
        });

    return;
}
</script>

<template>
    <div class="d-flex flex-column justify-center align-center">
        <div
            class="d-flex justify-end mb-10"
            style="font-size: 30px; color: white"
        >
            다음 글을 읽고 이어질 이야기를 작성하세요
        </div>
        <v-card
            class="d-flex flex-column justify-center align-center"
            width="500"
            height="500"
            style="background-color: #ffe280"
        >
            <CountdownTimer v-if="!isTitle" @timesUp="handleTimesUp" />

            <GenericInput
                v-if="isTitle"
                hint="동화에 제목을 입력하세요"
                v-model="title"
                class="textarea"
            />

            <SubmitButton
                v-if="isTitle"
                data="제목 제출하기"
                @click-event="titleSubmit"
            />

            <GenericInputArea
                v-if="!isTitle"
                class="textarea"
                v-model="textareaValue"
                placeholder="글을 작성해주세요"
            />
        </v-card>
    </div>
</template>

<style scoped>
.textarea {
    background-color: #ffffff;
    width: 90%;
    height: 100px;
}

.custom-textarea .v-input__details {
    height: 0px;
}
</style>
