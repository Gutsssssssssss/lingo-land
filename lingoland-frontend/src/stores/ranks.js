import { ref, computed } from "vue";
import { defineStore } from "pinia";
// import axios from 'axios'

export const useRankStore = defineStore("rank", () => {
  const ranks = ref([]);
  const selectedRank = ref(null);

  const rankOrdering = () => {
    /* data 받아오는 거 해야할 듯 순위 받아오는 알고리즘? */
  };
  // 선택된 랭커의 프로필을 보여주기 위함
  const clickRank = (rank) => {
    selectedRank.value = rank;
  };

  return { ranks, clickRank, selectedRank };
});
