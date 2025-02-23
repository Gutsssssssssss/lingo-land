<script setup>
import { writingGameConfiguration } from "@/configuration/writingGameConf";
import { useOpenviduStore } from "@/stores/openvidu";
import { useWritingGameStore } from "@/stores/writingGame";
import { storeToRefs } from "pinia";
import Swal from "sweetalert2";
import { computed, ref, watch } from "vue";

const writingGameStore = useWritingGameStore();
const { turn, totalTime, pageCount } = storeToRefs(writingGameStore);

const openviduStore = useOpenviduStore();
const { findCurrentStoryId } = openviduStore;
const { storyList } = storeToRefs(openviduStore);

const currentStorys = ref(null);

// 내 커넥션 아이디를 찾고
// 현재 턴에 해당하는 이야기아이디를 찾고
// 찾은 이야기 아이디를 키로 갖는 아이템을 찾아야한다.
function setStory() {
    // 현재 내가 봐야할 스토리 아이디 찾기
    let currentStoryId = findCurrentStoryId();

    for (let index = 0; index < storyList.value.length; index++) {
        if (storyList.value[index].storyId === currentStoryId) {
            currentStorys.value = storyList.value[index].story;
            return;
        }
    }
}

// watch를 써서 turn의 변화를 보고 있다가 turn이 바뀌면 보여주는 글을 바꾼다.
// turn이 변경됨을 감지
watch(turn, (newValue, oldValue) => {
    // setStory 함수 실행
    console.log("************turn이 바뀜", oldValue, newValue);

    if (newValue < pageCount.value) {
        // 턴이 바뀌면 alert하고 타이머 재시작 그리고 글쓰기 페이지
        Swal.fire({
            title: "새로운 이야기가 도착했어요",
            icon: "success",
            confirmButtonText: "완료",
            timer: writingGameConfiguration.alertTime,
        }).then(() => {
            // 턴이 바뀌었을 때 다음 사람이 작성할 이야기 세팅
            setStory();
            // 타이머 초기화
            totalTime.value = writingGameConfiguration.gameTime + (turn.value * 30); // 턴이 진행됨에 따라서 시간 가중치
        });
    }
});

const computedStorys = computed(() => {
    const storys =
        currentStorys.value === null
            ? ["나만의 이야기를 시작하세요"]
            : currentStorys.value;

    return storys;
});
</script>

<template>
    <div class="scroll">
        <div
            class="story"
            v-for="(story, index) in computedStorys"
            :key="index"
        >
            <div style="font-weight: 700; width: 80%">
                {{ story }}
            </div>
        </div>
    </div>
</template>

<style scoped>
.scroll {
    position: absolute;
    top: 10%;
    max-height: 70%;
    overflow-y: auto;
    overflow-x: hidden;
    width: 80%;
}

.story {
    position: relative;
    top: 10%;
    left: 20%;
    margin-bottom: 20px;
}

.scroll::-webkit-scrollbar {
    width: 5px;
    /* 스크롤바의 너비 */
}

.scroll::-webkit-scrollbar-track {
    background: rgb(255, 255, 255, 1);
}

.scroll::-webkit-scrollbar-thumb {
    background: #c2c2c2;
    /* 핸들의 색상 */
    border-radius: 10px;
    /* 핸들의 모서리 둥글기 */
}
</style>
