import { defineStore, storeToRefs } from "pinia";
import { computed, inject, ref } from "vue";
import { useOpenviduStore } from "../openvidu";

export const useGameStore = defineStore("gameStore", () => {
    const axios = inject("axios");

    const openviduStore = useOpenviduStore();
    const { reparticipants } = storeToRefs(openviduStore);

    const zCoordinate = ref(0); // z 위치
    const zDivided = computed(() => zCoordinate.value / 75); // 게임진행률

    const isGameEnded = ref(false); // 게임 종료

    const gameRanks = ref([]);
    const sortedRanks = computed(() => {
        return [...gameRanks.value].sort((a, b) => b.score - a.score);
    });
    const cameraAnimationStartTime = ref(0);
    const coinScore = ref(0);
    const autoForwardSpeed = ref(0);
    const coinTotalScore = ref(0);
    const start = ref(false)
    const wrongProblem = ref([]);

    const startfunc = ref(false);

    const problemIndex = ref(0);

    const elapsedTime = ref(0)

    const startTime = ref(0)

    function updateZCoordinate(z) {
        zCoordinate.value = z;
    }
    function endGame() {
        isGameEnded.value = true;
    }

    function saveResult(problemList) {
        axios
            .post("/problems/save-results", problemList, {
                withCredentials: true,
            })
            .then((response) => {
                console.log(response);
            })
            .catch((error) => {
                console.log(error);
            });
    }

    function setGameRanks() {
        for (const participant of reparticipants.value) {
            gameRanks.value.push({
                connectionId: participant.connectionId,
                userId: participant.userId,
                score: 0,
                coin: 0,
                nickname: participant.userProfile.nickname,
                problemList: wrongProblem,
            });
        }
    }

    function resetRunningGame() {
        console.log("**************달리기 게임 재설정");

        zCoordinate.value = 0;
        isGameEnded.value = false;
        gameRanks.value = [];
        wrongProblem.value = [];
        coinScore.value = 0;
        coinTotalScore.value = 0;
        startfunc.value = false;
        problemIndex.value = 0;
        elapsedTime.value = 0; // 타이머 시간 실시간 표시
        start.value= false
        startTime.value = 0
        cameraAnimationStartTime.value = 0
        autoForwardSpeed.value = 0

        setGameRanks();
    }

    return {
        cameraAnimationStartTime,
        autoForwardSpeed,
        startTime,
        start,
        zCoordinate,
        zDivided,
        isGameEnded,
        gameRanks,
        sortedRanks,
        wrongProblem,
        coinScore,
        coinTotalScore,
        startfunc,
        problemIndex,
        elapsedTime,
        updateZCoordinate,
        endGame,
        saveResult,
        setGameRanks,
        resetRunningGame,
    };
});
