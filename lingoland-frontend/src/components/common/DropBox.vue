<script setup>
import { defineProps } from "vue";
/*
 id: 컴포넌트 id
 label: dropbox 라벨 이름
 item: dropbox 컴포넌트에서 사용할 세부 아이템 리스트
*/
const props = defineProps({
    id: String,
    hint : String,
    item: Array,
    // width: Number,
});
</script>

<template>
    <v-select
        clearable
        variant="outlined"
        width="300px"
        color="black"
        :placeholder="hint"
        density="compact"
        :items="item"
    ></v-select>
</template>

<style scoped></style>
