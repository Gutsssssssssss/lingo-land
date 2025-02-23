<script setup>
import { storeToRefs } from "pinia";
import { onMounted } from "vue";

// 초기 세팅
import router from "@/router";
import { useGameStore } from "@/stores/runningGame/gameStore";
import { soundbg } from "@/stores/runningGame/init";
import { initDraw } from "@/stores/runningGame/resultinit";
import { useRoute } from "vue-router";
import RankListItem from "./RankListItem.vue";

const route = useRoute();

// 카운트다운 & 타이머
// const result = sortedRanks
// 문제

const gameStore = useGameStore();
const { sortedRanks, wrongProblem, coinTotalScore } = storeToRefs(gameStore);

onMounted(() => {
    initDraw();

    const result = {
        problemList: wrongProblem.value,
        coinCount: coinTotalScore.value,
    };

    gameStore.saveResult(result);

    setTimeout(() => {
        console.log("게임룸으로 푸쉬");
        soundbg.stop();
        router.push({
            name: "gameRoom",
            params: { roomId: route.params.roomId },
        });
    }, 10000);
});
</script>
<template>
    <div id="game-container">
        <canvas id="c"></canvas>

        <div id="leaderboard-container">
            <v-card
                class="pt-3 ma-30"
                height="75vh"
                style="background-color: transparent"
            >
                <v-row>
                    <v-col>
                        <h1 class="ml-10" style="color: aliceblue">
                            <span
                                class="material-symbols-outlined"
                                style="font-size: xx-large"
                            >
                                military_tech
                            </span>
                            순위
                        </h1>
                    </v-col>
                </v-row>
                <v-expansion-panels
                    class="d-flex pa-4 member-list"
                    variant="popout"
                    width="100"
                >
                    <v-expansion-panel
                        v-for="(rank, i) in sortedRanks"
                        :key="i"
                        hide-actions
                        bg-color="#5c822f"
                    >
                        <v-row class="d-flex align-center px-5">
                            <v-col cols="2">
                                <span>{{ i + 1 }}등 </span>
                            </v-col>
                            <v-col>
                                <RankListItem
                                    :rank="rank"
                                    style="color: black"
                                />
                            </v-col>
                        </v-row>
                    </v-expansion-panel>
                </v-expansion-panels>
            </v-card>
        </div>
    </div>
    <link
        rel="stylesheet"
        href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0"
    />
</template>

<style scoped>
/* Include necessary imports */
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

#leaderboard-container {
    position: absolute;
    top: 70px; /* Adjust vertical position */
    left: 120px; /* Adjust horizontal position */
    width: 450px; /* Width of the leaderboard card */
    /* background-color: rgba(
        172,
        204,
        124,
        0.5
    ); Semi-transparent background */
    background-color: rgb(67, 54, 49, 0.8);
    z-index: 10; /* Ensure it's above the canvas */
    padding: 10px; /* Add padding if needed */
    border-radius: 8px; /* Optional: rounded corners */
}

.v-card {
    background-color: rgba(
        255,
        255,
        255,
        0.8
    ); /* Semi-transparent white background */
    color: #000; /* Text color inside v-card */
    border-radius: 8px; /* Rounded corners */
    padding: 10px; /* Padding inside v-card */
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3); /* Optional: shadow for better visibility */
}

#progress-bar {
    position: absolute;
    bottom: 20px;
    left: 50%;
    transform: translateX(-50%);
    width: 80%;
}

button {
    margin: 5px;
    padding: 10px 20px;
    font-size: 16px;
    color: white;
    background-color: rgb(255, 255, 255, 0.7); /* Green color */
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

button:hover {
    background-color: #218838; /* Darker green color */
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

.member-list {
    height: auto;
    max-height: 70%; /* Maximum height */
    overflow-y: auto;
}
</style>
