<script setup>
import { computed, onMounted, ref, watch } from "vue";
import WritingResult from "./WritingResult.vue";
import freeImage from "@/assets/free.png";
import router from "@/router";
import { useRoute } from "vue-router";
import sample from "/sampleBook.jpg";
import { useWritingGameStore } from "@/stores/writingGame";
import { storeToRefs } from "pinia";

const writingGameStore = useWritingGameStore();

const { tales } = storeToRefs(writingGameStore);

const route = useRoute();

const drawer = ref(true);
const rail = ref(true);

onMounted(() => {
    console.log(tales.value);
});

const selectedIndex = ref(0);
const selectedTaleDetail = computed(() => {
    console.log("클릭");
    return tales.value[selectedIndex.value];
});
</script>

<template>
    <v-navigation-drawer
        v-model="drawer"
        :rail="rail"
        permanent
        class="drawer gowun-batang-regular"
    >
        <v-list density="compact" @click="rail = !rail" nav>
            <v-list-item
                v-for="(tale, index) in tales"
                :key="index"
                class="d-flex justify-center align-center row mt-2"
            >
                <div
                    class="cover d-flex justify-center align-center"
                    @click="selectedIndex = index"
                ></div>
                <v-list-item-content>
                    <v-list-item-title :style="{ color: 'white' }">
                        {{ tale.title }}
                    </v-list-item-title>
                </v-list-item-content>
            </v-list-item>
        </v-list>
        <button
            @click="
                () => {
                    router.push({
                        name: 'gameRoom',
                        params: { roomId: route.params.roomId },
                    });
                }
            "
            style="position: absolute; bottom: 10px; left: 15px"
        >
            <span
                class="material-symbols-outlined"
                style="color: white; font-weight: 200; font-size: 30px"
            >
                door_open
            </span>
        </button>
    </v-navigation-drawer>
    <v-main class="main d-flex align-center justify-center">
        <WritingResult :tale="selectedTaleDetail" />
    </v-main>

    <link
        rel="stylesheet"
        href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0"
    />
</template>

<style scoped>
.main {
    background-image: url(/bookStore.jpg);
    background-size: cover;
    height: 100vh;
    width: 100vw;
}

.drawer {
    background-image: url(/drawer2.jpg);
    background-size: cover;
    background-repeat: no-repeat;
    background-color: rgba(0, 0, 0, 0.6); /* This adds a dark overlay */
    background-blend-mode: multiply; /* This blends the dark color with the image */
    height: auto;
}

.cover {
    background-image: url(/bookCover.jpg);
    background-size: cover;
    height: 80px;
    margin: 2px;
    box-shadow: 10px 10px 10px rgb(0, 0, 0, 0.5);
}
.cover:hover {
    box-shadow: 5px -5px 5px rgb(255, 255, 255, 0.8);
}

.row {
    height: 105px;
}

.gowun-batang-regular {
    font-family: "Gowun Batang", serif;
    font-size: 10px;
}
</style>
