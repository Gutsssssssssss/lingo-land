<script setup>
import { defineProps, defineEmits } from "vue";
/*
 id: 컴포넌트 id
 type: button 컴포넌트 type 지정
 data: button 컴포넌트에 지정할 이름
  color: 색상
*/
defineEmits(["clickEvent"]);

const props = defineProps({
    id: String,
    type: String,
    data: String,
    height: String,
});
</script>

<template>
    <v-btn
        :id="id"
        color="#FFF1C2"
        :height="height"
        @click="$emit('clickEvent')"
        >{{ data }}</v-btn
    >
</template>

<style scoped></style>
