<script setup>
import { useGameStore } from "@/stores/runningGame/gameStore";
import { storeToRefs } from "pinia";
import { onMounted } from "vue";

// 초기 세팅
import { initDraw } from "@/stores/runningGame/init";
import { selectedAnswerIndex } from "@/stores/runningGame/model";

// 카운트다운 & 타이머
import { countdown } from "@/stores/runningGame/time";

// 문제
import {
    currentQuestion,
    isCorrect,
    loadQuestions,
    options,
    questionCountDown,
    questions,
    updateQuestion,
} from "@/stores/runningGame/question";

const gameStore = useGameStore();
const { sortedRanks, zDivided, startfunc, problemIndex } = storeToRefs(gameStore);

onMounted(() => {
    console.log("***********************달리기 게임이 계속 불리나?");

    gameStore.resetRunningGame();

    initDraw();
    loadQuestions(); // 문제 로드
    const interval = setInterval(() => {
        if (problemIndex.value === questions.value.length) {
            clearInterval(interval);
        }
        updateQuestion();
        console.log("문제 부름");
        console.log(currentQuestion.value);
    }, 15000);
});
</script>

<template>
    <div class="gowun-batang-regular">
        <div id="game-container">
            <canvas id="c"></canvas>
            <div id="timer">00:00:00</div>
            <div id="countdown" v-if="countdown > 0 && startfunc == true">
                {{ countdown }}
            </div>

            <v-row>
                <v-col class="d-flex align-center justify-center text-h5 my-1">
                    <div id="progress-bar">
                        <v-progress-linear
                            rounded
                            height="25"
                            color="rgb(92, 130,47)"
                            font-color="white"
                            :model-value="zDivided"
                        >
                            <template v-slot:default="{ value }">
                                <div class="text-button">
                                    {{ Math.ceil(value) }}%
                                </div>
                            </template>
                        </v-progress-linear>
                    </div>
                </v-col>
            </v-row>
        </div>

        <div v-if="currentQuestion" id="quiz-container">
            <h2>{{ currentQuestion.problem }}</h2>
            <div v-if="questionCountDown > 0">{{ questionCountDown }}</div>
            <ul class="no_dot d-flex justify-center">
                <li
                    v-for="(option, index) in options"
                    :key="index"
                    :class="{ highlighted: selectedAnswerIndex === index }"
                >
                    <button>
                        {{ option }}
                    </button>
                </li>
            </ul>
        </div>
        <div v-if="isCorrect !== null" id="quiz-containers">
            <img
                v-if="isCorrect"
                src="/correct.png"
                alt="Correct"
                class="result-image"
            />
            <img v-else src="/wrong.png" alt="Wrong" class="result-image" />
        </div>

        <div id="ranks-container" class="gamja-flower-regular">
            <h2 class="pl-2" style="color: white">순위</h2>
            <ul class="no_dot">
                <li
                    v-for="(rank, index) in sortedRanks"
                    :key="rank.connectionId"
                    :class="
                        index % 2 === 0 ? 'white-background' : 'gray-background'
                    "
                >
                    <span style="font-size: 30px"> {{ index + 1 }}등 </span>
                    <strong>{{ rank.nickname }}</strong>

                    <div style="font-size: 18px">
                        {{ rank.userId }} :
                        {{ Math.floor(rank.score * 100) }} 점
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Gowun+Batang&display=swap");
.gowun-batang-regular {
    font-family: "Gowun Batang", serif;
    font-weight: 500;
    font-style: normal;
    font-size: large;
}

.white-background {
    background-color: white;
    /* margin: 5px; */
    padding-left: 10px;
    margin-bottom: 10px;
    border-radius: 5px;
}

.gray-background {
    background-color: rgb(92, 130, 47, 0.5);
    border-radius: 5px;
    /* margin: 5px; */
    padding-left: 10px;
    margin-bottom: 10px;
    color: white;
}

/* Add any additional styles here */
#ranks-container {
    position: absolute;
    top: 15%;
    bottom: auto;
    right: auto;
    left: 10px;
    width: 200px;
    /* background-color: rgb(255,255,255,0.5); */
    background-color: rgb(67, 54, 49, 0.8);

    color: black;
    padding: 10px;
    border-radius: 5px;
}
.highlighted button {
    background-color: #ffe280;
    color: black;
}
.no_dot {
    list-style-type: none;
}
#game-container {
    position: relative;
    width: 100%;
    height: 100%;
}

#c {
    width: 100%;
    height: 100%;
    display: block;
}

#timer {
    position: absolute;
    top: 10px;
    left: 10px;
    color: white;
    font-size: 20px;
    background-color: rgb(67, 54, 49, 0.8);

    padding: 5px;
    border-radius: 5px;
}

#progress-bar {
    position: absolute;
    bottom: 60px;
    left: 50%;
    transform: translateX(-50%);
    width: 80%;
    color: white;
}
#quiz-container {
    position: absolute;
    top: 20%;
    left: 50%;
    transform: translate(-50%, -50%); /* 중앙 정렬 */
    width: 900px; /* 원하는 너비 */
    padding: 20px;
    background-color: rgb(67, 54, 49, 0.5);

    border-radius: 10px; /* 모서리 둥글게 */
    text-align: center; /*텍스트 중앙 정렬 */
    justify-content: bottom;
    color: white;
    z-index: 1000;
}
#quiz-containers {
    position: absolute;
    top: 20%;
    left: 50%;
    transform: translate(-50%, -50%); /* 중앙 정렬 */
    /*width: 1000px;  원하는 너비 
  padding: 20px;*/
    /* background-color: rgba(0, 0, 0, 0.7); /* 배경 색상 */
    /* border-radius: 10px; 모서리 둥글게 */
    text-align: center; /*텍스트 중앙 정렬 */
    justify-content: center;
    /* color: white; */
    z-index: 1000;
}
button {
    margin: 5px;
    padding: 10px 20px;
    font-size: 16px;
    color: white;
    /* background-color: #5c822f; */
    background-color: rgb(67, 54, 49);
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

button:hover {
    background-color: #0056b3;
}

p {
    font-size: 18px;
    margin-top: 10px;
}
/* .coordinates {
    position: absolute;
    top: 10px;
    right: 10px;
    color: white;
    font-size: 20px;
    background-color: rgba(0, 0, 0, 0.5);
    padding: 5px;
    border-radius: 5px;
} */
#countdown {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    font-size: 5rem;
    color: white;
    background-color: rgba(0, 0, 0, 0.5);
    padding: 20px;
    border-radius: 10px;
}

.result-image {
    width: 800px;
    height: auto;
}
</style>
