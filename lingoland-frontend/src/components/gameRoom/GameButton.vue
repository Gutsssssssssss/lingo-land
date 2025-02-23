<script setup>
import { defineProps } from "vue";

const props = defineProps({
    img: String,
    name: String,
    desc: String,
    clickEvent: String,
});
</script>

<template>
    <button @click="$emit('clickEvent')" id="game-btn">
        <div>
            <img :src="img" width="90%" />
        </div>
        <div height="10%">{{ name }}</div>
        <div style="font-size:large">{{ desc }}</div>
    </button>
</template>

<style scoped>
#game-btn {
    width: 40%;
    height: 90%;
    background-color: white;
    border-radius: 1%;
    box-shadow: 7px 7px 5px rgba(0, 0, 0, 0.25);
}
</style>
