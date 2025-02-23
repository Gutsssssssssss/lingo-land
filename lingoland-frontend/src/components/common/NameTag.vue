<script setup>
import { defineProps } from "vue";
/*
 id: 컴포넌트 id
 type: button 컴포넌트 type 지정
 data: button 컴포넌트에 지정할 이름 
*/
const props = defineProps({
    id: String,
    type: String,
    data: String,
});
</script>

<template>
    <div class="border-md nameTag">
        {{ data }}
    </div>
</template>

<style scoped>
.nameTag{
    background-color: #4C4637;
    color :white;
    height : 60px;
    width : 200px;
    display : flex;
    justify-content: center;
    align-items: center;
    border-top-left-radius: 10px;
    border-top-right-radius: 10px;
    /* font-size : 25px; */
    padding : 10px;
}
</style>