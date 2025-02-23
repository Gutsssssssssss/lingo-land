<script setup>
import { useTaleStore } from "@/stores/tales";
import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";

const { VITE_SERVER_IMAGE_URL } = import.meta.env;

const route = useRoute();
const taleStore = useTaleStore();
const tale = ref(null);
const activePage = ref(1);

const nextPage = () => {
    console.log("나옴");
    if (activePage.value < tale.value.content.length + 1) {
        activePage.value++;
    }
};

const prevPage = () => {
    if (activePage.value > 1) {
        activePage.value--;
    }
};

const handlePageClick = (event, pageNumber) => {
    console.log("눌림");
    if (pageNumber === activePage.value) {
        nextPage();
    } else if (pageNumber === activePage.value - 1) {
        prevPage();
    }
};

onMounted(() => {
    taleStore.oneTaleById(route.params.bookId).then((responseValue) => {
        tale.value = responseValue;

        console.log("tale", tale.value);
        for (let index = 0; index < tale.value.content.length; index++) {
            tale.value.content[index].illustration =
                VITE_SERVER_IMAGE_URL + tale.value.content[index].illustration;
        }
    });
});

watch(activePage, (newPage) => {
    const sceneElement = document.querySelector(".scene");
    if (newPage === 1) {
        sceneElement.classList.add("centered");
    } else {
        sceneElement.classList.remove("centered");
    }
});
</script>

<template>
    <!-- 뒤로가기 -->
    <!-- 책 -->
    <div v-if="tale" class="scene centered gowun-batang-regular">
        <div class="book" ref="book">
            <!-- 첫 번째 페이지 (커버) -->
            <div
                class="page"
                :class="{
                    active: activePage === 1,
                    flipped: activePage > 1,
                }"
                @click="(event) => handlePageClick(event, 1)"
            >
                <div class="front-cover">
                    <h1>{{ tale.title }}</h1>
                    <img
                        class="cover-qr"
                        :src="VITE_SERVER_IMAGE_URL + tale.cover"
                    />
                    <div id="carbon-block"></div>
                </div>
                <div class="back">
                    <img class="qr" :src="tale.content[0].illustration" />
                    <h3>– 1 –</h3>
                </div>
            </div>

            <!-- 나머지 페이지 -->
            <div
                v-for="(content, index) in tale.content"
                :key="index"
                class="page"
                :class="{
                    active: activePage === index + 2,
                    flipped: activePage > index + 2,
                }"
                @click="(event) => handlePageClick(event, index + 2)"
            >
                <div class="front d-flex justify-center align-center">
                    <p>
                        {{ content.story }}
                    </p>
                </div>
                <div class="back">
                    <img v-if="index +1 < tale.content.length" class="qr" :src="tale.content[index+1].illustration" />
                    <h3>– {{ index + 2 }} –</h3>
                </div>
            </div>
        </div>
    </div>
    <link
        rel="stylesheet"
        href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200"
    />
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Gowun+Batang&display=swap");
.gowun-batang-regular {
    font-family: "Gowun Batang", serif;
    font-weight: 500;
    font-style: normal;
    font-size: large;
}

h1 {
    text-align: center;
    margin-top: 10px;
}

h3 {
    position: absolute;
    bottom: 20px;
    left: 50%;
    transform: translateX(-50%);
    text-align: center;
    margin: 0;
}

.scene {
    width: 45%;
    height: 80%;
    margin: 2% 2% 2% 50%;
    perspective: 1000px;
    transition: transform 1.5s ease;
}

.scene.centered {
    position: fixed;
    transform: translateX(-50%);
    left: 10px;
}

.book {
    position: relative;
    width: 100%;
    height: 100%;
    transform-style: preserve-3d;
}

.page {
    cursor: pointer;
    position: absolute;
    color: black;
    width: 100%;
    height: 100%;
    transition: 1s transform;
    transform-style: preserve-3d;
    transform-origin: left center;
}

.front,
.back {
    position: absolute;
    width: 100%;
    height: 100%;
    padding: 1% 1% 1% 1%;
    box-sizing: border-box;
    backface-visibility: hidden;
    background: linear-gradient(to bottom right, #fff1c2, #ffffff);
}

.front-cover {
    position: absolute;
    width: 100%;
    height: 100%;
    padding: 5% 5% 5%;
    box-sizing: border-box;
    box-shadow: -5px -5px 15px rgb(0, 0, 0, 0.8);
    backface-visibility: hidden;
    background-image: url(/bookCover.jpg);
    background-size: cover;
}

.back {
    transform: rotateY(180deg);
}

.page.active {
    z-index: 1;
}

.page.flipped {
    transform: rotateY(-180deg);
}

.page.flipped:last-of-type {
    z-index: 1;
}

p {
    margin: 50px;
    text-indent: 1em;
    line-height: 1.7;
}

.qr {
    display: block;
    margin: 10px auto;
    max-width: 100%;
    height: 90%;
    max-height: 95%;
}

.cover-qr {
    display: block;
    margin: 10px auto;
    max-width: 100%;
    height: 80%;
    /* max-height: 95%; */
}
</style>
