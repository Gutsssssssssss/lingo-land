<script setup>
import { ref } from "vue";
import BackButton from "@/components/common/BackButton.vue";
import TaleDetail from "@/components/tale/TaleDetail.vue";
import bookStore from "/bookStore.jpg";

// 밝기 상태를 관리하는 ref 생성

// 배경을 어둡게/밝게 전환하는 함수
</script>

<template>
    <div class="main">
        <BackButton />
        <TaleDetail class="gowun-batang-regular" />
    </div>
</template>

<style scoped>
.main {
    background-image: url(/bookStore.jpg);
    background-size: cover;
    height: 100vh;
    width: 100vw;
    transition: filter 1s ease; /* 필터 애니메이션 적용 */
}


</style>
