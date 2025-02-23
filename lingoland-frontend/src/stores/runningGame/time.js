import { ref } from "vue";
import { useGameStore } from "./gameStore";
import { storeToRefs } from "pinia";

const gameStore = useGameStore();
const { elapsedTime } = storeToRefs(gameStore);
const countdown = ref();
let timerInterval = null;
let startTime = null;
console.log("체크중",elapsedTime)

function startCountdown() {
  countdown.value = 3;

  const interval = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--;
    } else {
      clearInterval(interval);

      startTimer(); // 카운트다운 완료 후 타이머 시작
    }
  }, 1000);
}

function startTimer() {
  startTime = Date.now();
  timerInterval = setInterval(updateTimer(startTime), 1000);
  console.log("시간 시작!!!!!!!!!1");
}

function updateTimer(startTime) {
  const timerElement = document.querySelector("#timer");
  elapsedTime.value = Date.now() - startTime;

  const milliseconds = Math.floor((elapsedTime.value % 1000) / 10); // 밀리초 단위를 2자리로 표시하기 위해 10으로 나눔
  const seconds = Math.floor((elapsedTime.value / 1000) % 60);
  const minutes = Math.floor((elapsedTime.value / (1000 * 60)) % 60);

  timerElement.textContent = `${String(minutes).padStart(2, "0")}:${String(
    seconds
  ).padStart(2, "0")}:${String(milliseconds).padStart(2, "0")}`;

}

export {
  countdown,
  startCountdown,
  startTime,
  startTimer,
  timerInterval,
  updateTimer,
};
