<script setup>
import { defineProps } from "vue";
import GameMemberListItem from "./GameMemberListItem.vue";

const props = defineProps({
    members: Array,
});
</script>

<template>
    <v-list class="game-member-list">
        <v-list-item v-for="(member, index) in members" :key="index">
            <GameMemberListItem
                :member="member.userProfile"
            ></GameMemberListItem>
        </v-list-item>
    </v-list>
</template>

<style scoped>
.game-member-list {
    max-height: 100%; /* 최대 높이 설정 */
    overflow-y: auto; /* 수직 스크롤을 가능하게 설정 */
} /* width */

::-webkit-scrollbar {
    width: 10px;
}

/* Track */
::-webkit-scrollbar-track {
    box-shadow: inset 0 0 5px grey;
    border-radius: 10px;
}

/* Handle */
::-webkit-scrollbar-thumb {
    background: rgb(252, 194, 85);
    border-radius: 10px;
}
</style>
