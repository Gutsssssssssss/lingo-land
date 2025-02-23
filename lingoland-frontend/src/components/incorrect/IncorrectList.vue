<script setup>
import { useProblemStore } from "@/stores/problem";
import { defineProps, onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import IncorrectListItem from "./IncorrectListItem.vue";

const emit = defineEmits(["clickEvent"]);

const problemStore = useProblemStore();
const route = useRoute();
const incorrectList = ref();

const props = defineProps({
    groupId: String,
    memberId: String,
});

function getWrongProblems() {
    let problemList;
    if (route.params.memberId) {
        problemList = problemStore.getWrongProblemsByadmin(
            props.groupId,
            props.memberId
        );
    } else {
        problemList = problemStore.getMyWrongProblems();
    }

    if (problemList) {
        problemList.then((getvalue) => {
            incorrectList.value = getvalue;
        });
    }
}

function completeWrongProblem() {
    emit("clickEvent");
    // 갱신된 오답노트 재요청
    getWrongProblems();
}

onMounted(() => {
    // 오답노트 가져오기
    getWrongProblems();
});
</script>

<template>
    <v-list class="incorrect-list">
        <v-list-item v-for="(incorrect, index) in incorrectList" :key="index">
            <IncorrectListItem
                :incorrect="incorrect"
                @click-event="completeWrongProblem"
                :member-id="memberId"
            ></IncorrectListItem>
        </v-list-item>
    </v-list>
</template>

<style scoped>
.incorrect-list {
    max-height: 50%; /* 최대 높이 설정 */
    overflow-y: auto; /* 수직 스크롤을 가능하게 설정 */
}
/* width */
::-webkit-scrollbar {
    width: 10px;
}

/* Track */
::-webkit-scrollbar-track {
    box-shadow: inset 0 0 5px grey;
    border-radius: 10px;
}

/* Handle */
::-webkit-scrollbar-thumb {
    background: rgb(252, 194, 85);
    border-radius: 10px;
}

.custom-swal-popup {
    z-index: 99999 !important; /* Vue 다이얼로그보다 높은 z-index 설정 */
}
</style>
