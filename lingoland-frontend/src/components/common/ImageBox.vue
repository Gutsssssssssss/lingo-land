<script setup>
import { defineProps } from "vue";

/*

 aspectRatio: String, 이미지 가로세로 비율
 height: Number, 높이
 width: Number, 폭
 source: String, 이미지 소스
 
*/

const props = defineProps({
    source: String,
});


</script>

<template>
    <div class="imagebox-container">
      <v-img
        :src="source"
        class="responsive-img rounded-circle"
        cover
      ></v-img>
    </div>
  </template>
  
  <style scoped>
  .imagebox-container {
    width: 100%;
    max-width: 300px; /* 기본 크기를 300px로 설정 */
    aspect-ratio: 1/1; /* 이미지 비율 유지 */
  }
  
  .responsive-img {
    width: 100%;
    height: auto;
    aspect-ratio: inherit; /* 부모의 비율을 상속 */
  }
  
  </style>