<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { useGameStore } from "@/stores/runningGame/gameStore";
import { storeToRefs } from "pinia";
import {OpenVidu} from "openvidu-browser";
// 초기 세팅
import { initDraw } from "@/stores/runningGame/init";

// 카운트다운 & 타이머
import { startCountdown, countdown } from "@/stores/runningGame/time";

// 문제
import {
  loadQuestions,
  currentQuestion,
  options,
  isCorrect,
  updateQuestion,
  questionCountDown,
} from "@/stores/runningGame/question";

const OV = new OpenVidu();
const session = OV.initSession();
const gameStore = useGameStore();
const { zCoordinate } = storeToRefs(gameStore);

//게임진행률
const zDivided = computed(() => zCoordinate.value / 90);

onMounted(() => {
  startCountdown();
  initDraw();
  loadQuestions(); // 문제 로드
  setInterval(() => {
    updateQuestion();
    console.log("문제 부름");
    console.log(currentQuestion.value);
  }, 9000);
});
</script>
<template>
  <div class="gowun-batang-regular">
    <!-- Canvas와 타이머를 포함하는 상위 div -->
    <div id="game-container">
      <canvas id="c"></canvas>
      <div id="timer">00:00:00</div>
      <div id="countdown" v-if="countdown > 0">{{ countdown }}</div>

      <v-row>
        <v-col class="d-flex align-center justify-center text-h5 my-1">
          <div id="progress-bar">
            <v-progress-linear
              rounded
              height="25"
              color="primary"
              :model-value="zDivided"
            >
              <template v-slot:default="{ value }">
                <div class="text-button">{{ Math.ceil(value) }}%</div>
              </template>
            </v-progress-linear>
          </div>
        </v-col>
      </v-row>
    </div>
    <div id="coordinates" class="coordinates"></div>

    <div v-if="currentQuestion" id="quiz-container">
      <h2>{{ currentQuestion.problem }}</h2>
      <div v-if="questionCountDown > 0">{{ questionCountDown }}</div>
      <ul class="no_dot d-flex justify-center">
        <li v-for="(option, index) in options" :key="index">
          <button>
            {{ option }}
          </button>
        </li>
      </ul>
    </div>
    <div v-if="isCorrect != null" id="quiz-container">
      <h2>
        {{ isCorrect ? "정답입니다!" : "틀렸습니다!" }}
      </h2>
    </div>
    <div v-if="questions === null" id="quiz-container">
      <p>퀴즈가 완료되었습니다!</p>
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

.no_dot {
  list-style-type: none;
}
#game-container {
  position: relative;
  width: 100%;
  height: 100vh;
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
  background-color: rgba(0, 0, 0, 0.5);
  padding: 5px;
  border-radius: 5px;
}

#progress-bar {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
}
#quiz-container {
  position: absolute;
  top: 20%;
  left: 50%;
  transform: translate(-50%, -50%); /* 중앙 정렬 */
  width: 1000px; /* 원하는 너비 */
  padding: 20px;
  background-color: rgba(0, 0, 0, 0.7); /* 배경 색상 */
  border-radius: 10px; /* 모서리 둥글게 */
  text-align: center; /*텍스트 중앙 정렬 */
  justify-content: center;
  color: white;
  z-index: 1000;
}

button {
  margin: 5px;
  padding: 10px 20px;
  font-size: 16px;
  color: white;
  background-color: #007bff;
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
.coordinates {
  position: absolute;
  top: 10px;
  right: 10px;
  color: white;
  font-size: 20px;
  background-color: rgba(0, 0, 0, 0.5);
  padding: 5px;
  border-radius: 5px;
}
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
</style>
