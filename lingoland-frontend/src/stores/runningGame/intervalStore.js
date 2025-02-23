import { storeToRefs } from "pinia";
import { ref } from "vue";
import { useGameStore } from "./gameStore";



// const openviduStore = useOpenviduStore();

const gameStore = useGameStore();
const { gameRanks, coinScore, coinTotalScore } =
    storeToRefs(gameStore);

console.log(gameRanks, coinScore, coinTotalScore)

export {
gameRanks,
coinScore,
coinTotalScore
};
