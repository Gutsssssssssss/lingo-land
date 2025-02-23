<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { storeToRefs } from "pinia";
import { OpenVidu } from "openvidu-browser";
// 초기 세팅
import { initDraw } from "@/stores/runningGame/resultinit";
import { useGroupMemberStore } from "@/stores/groupMember";
import RankListItem from "@/components/rank/RankListItem.vue";

const groupMemberStore = useGroupMemberStore();
// 카운트다운 & 타이머

// 문제

const OV = new OpenVidu();
const session = OV.initSession();

onMounted(() => {
  initDraw();
});
</script>
<template>
  <!-- Canvas와 타이머를 포함하는 상위 div -->
  <div id="game-container">
    <canvas id="c"></canvas>

    <!-- Leaderboard card -->
    <div id="leaderboard-container">
      <v-card class="pt-3 ma-30" height="75vh">
        <v-row>
          <v-col>
            <h1 class="ml-10">달리기 결과</h1>
          </v-col>
 
        </v-row>
        <v-expansion-panels class="d-flex pa-4 member-list" variant="popout" width="200">
          <v-expansion-panel
            v-for="(member, i) in groupMemberStore.members"
            :key="i"
            hide-actions
          >
            <!-- 아직 변수 값을 몰라 임의로 작성하였습니다. 데이터는 store에 임의로 작성하여 구성했습니다. -->
            {{ i+1 }}등<RankListItem :member="member" />
          </v-expansion-panel>
        </v-expansion-panels>
      </v-card>
    </div>
  </div>
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
  left: 70px; /* Adjust horizontal position */
  width: 800px; /* Width of the leaderboard card */
  background-color: rgba(172, 204, 124, 0.5); /* Semi-transparent background */
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
  background-color: rgb(255, 255, 255,0.7),; /* Green color */
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
