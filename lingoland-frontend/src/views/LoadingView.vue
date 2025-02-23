<script setup>
import LoadingBooks from "@/components/common/Loadingbooks.vue";
import { ref } from "vue";

// 밝기 상태를 관리하는 ref 생성
const isDimmed = ref(false);
</script>

<template>
    <div :class="['main', { dimmed: isDimmed }]">
        <LoadingBooks class="gowun-batang-regular" />
    </div>
</template>

<style>
#full {
    height: 100%;
}

.main {
    background-image: url(/bookStore.jpg);
    background-size: cover;
    height: 100vh;
    width: 100vw;
    transition: filter 1s ease; /* 필터 애니메이션 적용 */
}

/* 어두운 상태에 적용될 클래스 */
.dimmed {
    filter: brightness(50%);
}
</style>
