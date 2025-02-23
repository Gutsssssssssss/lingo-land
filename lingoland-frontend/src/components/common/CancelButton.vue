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
    height: String,
});
</script>

<template>
    <v-btn class="my-5" :id="id" variant="outlined":height="height" @click="$emit('clickEvent')">{{ data }}</v-btn>
</template>

<style scoped></style>
