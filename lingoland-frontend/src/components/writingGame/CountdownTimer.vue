<script setup>
import { writingGameConfiguration } from "@/configuration/writingGameConf";
import { computed, defineEmits, onBeforeUnmount, onMounted, watch } from "vue";
import { useWritingGameStore } from "@/stores/writingGame";
import { storeToRefs } from "pinia";

const emit = defineEmits(["timesUp"]);

const writingGameStore = useWritingGameStore();
const { totalTime, turn } = storeToRefs(writingGameStore);

let timer = null;

const minutes = computed(() => {
    const mins = Math.floor(totalTime.value / 60);
    return mins < 10 ? `0${mins}` : mins;
});

const seconds = computed(() => {
    const secs = totalTime.value % 60;
    return secs < 10 ? `0${secs}` : secs;
});

const startTimer = () => {
    // 1초마다 함수를 실행
    timer = setInterval(() => {
        if (totalTime.value > 0) {
            totalTime.value--;
        } else {
            clearInterval(timer);
            emit("timesUp"); // 타이머 종료 이벤트 발생
        }
    }, 1000);
};

onMounted(() => {
    startTimer();
});

onBeforeUnmount(() => {
    clearInterval(timer);
});

watch(totalTime, (newValue, oldValue) => {
    if (
        oldValue === 0 &&
        newValue === writingGameConfiguration.gameTime + turn.value * 30 // 턴이 진행됨에 따라 시간 가중치 부여
    ) {
        startTimer();
    }
});
</script>

<template>
    <div class="countdown-timer my-10">
        <div>{{ minutes }}분 {{ seconds }}초 남았어요!</div>
    </div>
</template>

<style scoped>
.countdown-timer {
    text-align: center;
    font-size: 1.4em;
}
</style>
