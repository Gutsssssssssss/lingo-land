import axios from "axios";
import { storeToRefs } from "pinia";
import { ref } from "vue";
import { useOpenviduStore } from "../openvidu";
import { useGameStore } from "./gameStore";

const { VITE_SERVER_URL } = import.meta.env;

const questions = ref([]);
const currentQuestion = ref(null);
const options = ref([]);
const isCorrect = ref(null);
let currentProblemId;

const openviduStore = useOpenviduStore();
const { session } = openviduStore;
const { reparticipants } = storeToRefs(openviduStore);

const gameStore = useGameStore();
const { gameRanks, wrongProblem, coinScore, coinTotalScore, problemIndex } =
    storeToRefs(gameStore);

let answer = null;
let answerTimeout = null;
let closeQuestionTimeout = null;
const shownQuestions = new Set(); // 이미 표시된 문제를 추적

// **openvidu signal
// 문제를 정답을 확인했다. -> 시그널을 보낸다.
const checkProblem = () => {
    session
        .signal({
            type: "checkProblem",
            data: JSON.stringify({
                score: isCorrect.value ? 1 : 0, // 점수
                isCorrect: isCorrect.value, // 문제를 맞췄다. 틀렸다. (T/F)
                answer: answer,
                problemId: currentProblemId, // 내가 작성한 답
            }),
        })
        .then(() => {
            console.log(currentProblemId);
            wrongProblem.value.push({
                problemId: currentProblemId,
                answer: answer,
            });
            console.log("저장중", wrongProblem.value);
        });
};

// 문제 정답 확인에 대한 시그널을 수신하는 함수
session.on("signal:checkProblem", (event) => {
    const problemResult = JSON.parse(event.data);

    // participants에서의 커넥션아이디와
    // event.from.connectionId가 같은 놈을 찾아서

    // 점수를 더해주면 된다.
    let lenPar = reparticipants.value.length;
    for (let i = 0; i < lenPar; i++) {
        if (gameRanks.value[i].connectionId === event.from.connectionId) {
            console.log("지금의 기록", problemResult);
            gameRanks.value[i].score += problemResult.score;
            gameRanks.value[i].score += coinScore.value;
            gameRanks.value[i].coin = coinTotalScore.value;
            coinScore.value = 0;
            break;
        }
    }
    console.log(event.from.connectionId);
    console.log("***************시그널 보낸애 점수 점수점수", gameRanks.value);
    console.log("점수점수 refcoinScore", coinScore.value);
});

const questionCountDown = ref(8);

function qcountdown() {
    questionCountDown.value = 8;

    const interval = setInterval(() => {
        if (questionCountDown.value > 0) {
            questionCountDown.value--;
        } else {
            clearInterval(interval);
        }
    }, 1000);
}

async function loadQuestions() {
    await axios({
        url: `${VITE_SERVER_URL}/problems`,
        method: "get",
        withCredentials: true,
    })
        .then((response) => {
            console.log(response);
            console.log("문제를 받아영", response);

            questions.value = response.data;
        })
        .catch((error) => {
            console.error("문제 로드 실패:", error);
        });
}

function updateQuestion() {
    if (problemIndex.value >= 0 && problemIndex.value < questions.value.length) {
        if (!currentQuestion.value) {
            //     // 이미 표시된 문제인지 확인하고 현재 문제가 없는지 확인
            loadQuestion();
            console.log(problemIndex.value);
            shownQuestions.add(problemIndex.value); // 표시된 문제로 기록
        }
    }
}

function loadQuestion() {
    if (questions.value.length > 0 && problemIndex.value < questions.value.length) {
        currentQuestion.value = questions.value[problemIndex.value];
        currentProblemId = currentQuestion.value.problemId;
        console.log("currentquestion", currentQuestion.value.problemId);
        options.value = [
            currentQuestion.value.choices[0]["text"],
            currentQuestion.value.choices[1]["text"],
            currentQuestion.value.choices[2]["text"],
        ];
        isCorrect.value = null; // 초기화
        // if (answerTimeout) clearTimeout(answerTimeout);
        // if (closeQuestionTimeout) clearTimeout(closeQuestionTimeout);
    }
    problemIndex.value++;
    qcountdown();

    answerTimeout = setTimeout(() => {
        checkAnswerAndTime();
    }, 8000);
}

function checkAnswer(selected) {
    if (currentQuestion.value === null) return;

    // console.log("정답 : ", currentQuestion.value.answer);
    // console.log("내가 고른 거 : ", selected);
    //정답 여부를 표시
    answer = selected;
}

function checkAnswerAndTime() {
    console.log("나는 불리는 중입니다!");
    isCorrect.value = answer == currentQuestion.value.answer;
    currentQuestion.value = null; // 문제 창 닫기
    closeQuestionTimeout = setTimeout(() => {
        checkProblem(); // 문제를 풀었을 때 시그널을 보낸다.
        isCorrect.value = null; // 정답 여부 초기화
        resetQuestionOnExit();
    }, 1000);
    // 정답 여부 표시 후 2초 뒤에 문제 창 닫기
}

function resetQuestionOnExit() {
    if (answerTimeout) clearTimeout(answerTimeout);
    if (closeQuestionTimeout) clearTimeout(closeQuestionTimeout);
}

export {
    checkAnswer,
    coinTotalScore,
    currentQuestion,
    gameRanks,
    isCorrect,
    loadQuestion,
    loadQuestions,
    options,
    questionCountDown,
    questions,
    resetQuestionOnExit,
    updateQuestion,
    wrongProblem,
    coinScore,
    reparticipants
};
