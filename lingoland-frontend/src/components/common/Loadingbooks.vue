<script setup>
import router from "@/router";
import { onBeforeUnmount, onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";
import localImage5 from "/loading/374e42c6-292f-47ee-836c-2d3e5cdec385.webp";
import localImage7 from "/loading/43abb3db-7584-4c36-93fe-4f1dd0887ebf.webp";
import localImage3 from "/loading/48c11390-d1b8-4955-802e-bcbaf5340511.webp";
import localImage2 from "/loading/5eb606a2-ef51-4a68-8fbd-208f1afcfb2e.webp";
import localImage4 from "/loading/782f7eb8-0399-40fe-af59-d792435e7af2.webp";
import localImage6 from "/loading/86b6135e-a120-4c36-8409-31173f7f4e97.webp";
import localImage1 from "/loading/8943773e-89de-41eb-9afa-dae01b597b35.webp";

const activePage = ref();
let intervalId = null;

const route = useRoute();

// WebP 이미지와 내용 데이터를 직접 정의
const mockTaleData = {
    cover: localImage1,
    content: [
        {
            story: "날이 밝은 어느 날... 6명의 사람이 우연한 기회로 모이게 되었습니다. 그 사람들은 다들 무언가 개발을 해보기로 결심했습니다.",
            illustration: localImage2,
        },
        {
            story: "그렇게 날이 새도록 회의를 하던 사람들은 결국 두 개의 의견으로 나뉘게 되었습니다.",
            illustration: localImage3,
        },
        {
            story: "선생님께도 물어보며 회의를 하고 두 의견을 가진 사람들은 두 개의 팀으로 나뉘어 싸우게 되었습니다. ",
            illustration: localImage4,
        },
        {
            story: "그러던 중 두 팀의 수장들은 서로 다른 팀의 팀원들을 은밀하게 설득했습니다.",
            illustration: localImage5,
        },
        {
            story: "그러던 끝에 결국 게임 개발을 주장하던 팀이 승리하게 되었고, 이 게임을 개발하게 되었습니다.",
            illustration: localImage6,
        },
        {
            story: "비록 직전에 한 명이 좋은 곳으로 떠나게 되었지만, 성공적으로 마칠 수 있었답니다~\n잘됐다네~잘됐다네~",
            illustration: localImage7,
        },
    ],
};

const tale = ref(mockTaleData);
const nextPage = () => {
    if (activePage.value < tale.value.content.length + 1) {
        activePage.value++;
    } else {
        activePage.value = 1; // 마지막 페이지 후 첫 페이지로 돌아갑니다.
    }
};

const prevPage = () => {
    if (activePage.value > 1) {
        activePage.value--;
    } else {
        activePage.value = tale.value.content.length + 1; // 첫 페이지 전에 마지막 페이지로 이동합니다.
    }
};

const handlePageClick = (event, pageNumber) => {
    if (pageNumber === activePage.value) {
        nextPage();
    } else if (pageNumber === activePage.value - 1) {
        prevPage();
    }
};

onMounted(() => {
    activePage.value = 1;
    intervalId = null;
    // 5초마다 페이지를 자동으로 넘기는 타이머 설정
    intervalId = setInterval(nextPage, 5000);
});

onBeforeUnmount(() => {
    // 컴포넌트가 언마운트될 때 타이머를 정리합니다.
    if (intervalId) {
        clearInterval(intervalId);
    }
});

watch(activePage, (newPage, prevPage) => {
    console.log("***newPage", newPage, "****prevPage", prevPage);
    const sceneElement = document.querySelector(".scene");
    if (newPage === 1) {
        sceneElement.classList.add("centered");
    } else {
        sceneElement.classList.remove("centered");
    }
    if (prevPage === 7 && newPage === 1) {
        router.push({
            name: "gameRoom",
            params: { roomId: route.params.roomId },
        });
    }
});
</script>

<template>
    <div
        class="text-h4 font-weight-black text-white d-flex justify-center mt-15"
    >
        우리가 쓴 동화를 만들고 있어요...
    </div>
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
                    <h1>학교에서 있었던 일</h1>
                    <img class="qr-c" :src="tale.cover" />
                    <div id="carbon-block"></div>
                </div>
                <div class="back">
                    <img
                        class="qr ill ma-0"
                        :src="tale.content[0].illustration"
                    />
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
                    <img
                        class="qr ma-0 ill"
                        v-if="index + 1 < tale.content.length"
                        :src="tale.content[index + 1].illustration"
                    />
                    <h3>– {{ index + 2 }} –</h3>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* 기존 스타일 유지 */
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

.ill {
    width: 100%;
    height: 100%;
    object-fit: cover;
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

.front,
.back {
    position: absolute;
    width: 100%;
    height: 100%;
    margin: 0 auto;
    padding: 2% 5% 5%;
    box-sizing: border-box;
    backface-visibility: hidden;
    background: linear-gradient(to bottom right, #fff1c2, #ffffff);
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
    margin: 50px auto;
    max-width: 100%;
    /* height: 70%; */
    max-height: 95%;
}
.qr-c {
    display: block;
    margin: 50px auto;
    max-width: 100%;
    height: 70%;
    max-height: 70%;
}
</style>
