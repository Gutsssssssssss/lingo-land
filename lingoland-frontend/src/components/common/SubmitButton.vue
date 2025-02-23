<script setup>
import { defineProps } from "vue";
/*
 id: 컴포넌트 id
 type: button 컴포넌트 type 지정
 data: button 컴포넌트에 지정할 이름
 color: 색상
*/
const props = defineProps({
    id: String,
    type: String,
    data: String,
    width: String,
});
</script>

<template>
    <v-btn
        :id="id"
        @click="$emit('clickEvent')"
        :width="width"
        color="#4C4637"
        >{{ data }}</v-btn
    >
</template>

<style scoped></style>
