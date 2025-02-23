<template>
    <div class="scene">
        <div class="book" ref="book">
            <div class="page" :class="{ active: activePage === 1, flipped: activePage > 1 }" @click="handlePageClick">
                <div class="front">
                    <h1>집에 가고 싶어요</h1>
                    <div>커버 이미지</div>

                    <div id="carbon-block"></div>
                </div>
                <div class="back">
                    <h1>– 1 –</h1>
                    <WritingResultListItem source="\src\assets\달리기.jpg" text="어쩌구 저쩌구" />
                </div>
            </div>
            <div class="page" :class="{ active: activePage === 2, flipped: activePage > 2 }" @click="handlePageClick">
                <div class="front">
                    <h1>– 2 –</h1>

                    <WritingResultListItem source="\src\assets\sampleImg.jpg" text="어쩌구 저쩌구" />
                </div>
                <div class="back">
                    <h1>– 3 –</h1>

                </div>
            </div>
            <div class="page" :class="{ active: activePage === 3, flipped: activePage > 3 }" @click="handlePageClick">
                <div class="front">
                    <h1>– 4 –</h1>

                </div>
                <div class="back">
                    <h1>– 5 –</h1>

                </div>
            </div>
            <div class="page" :class="{ active: activePage === 4, flipped: activePage > 4 }" @click="handlePageClick">
                <div class="front">
                    <h1>– 6 –</h1>

                </div>
                <div class="back">
                    <h1>– 7 –</h1>

                </div>
            </div>
            <div class="page" :class="{ active: activePage === 5, flipped: activePage > 5 }" @click="handlePageClick">
                <div class="front">
                    <h1>– 8 –</h1>

                </div>
                <div class="back">
                    <h1>– 9 –</h1>

                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import WritingResultListItem from './WritingResultListItem.vue';

const activePage = ref(1);
const totalPages = 5;
const book = ref(null);

const nextPage = () => {
    if (activePage.value < totalPages) {
        activePage.value++;
    }
    console.log('Next page:', activePage.value); // 디버그용 로그
};

const prevPage = () => {
    if (activePage.value > 1) {
        activePage.value--;
    }
    console.log('Previous page:', activePage.value); // 디버그용 로그
};

const handlePageClick = (event) => {
    const target = event.currentTarget;
    console.log('Page clicked:', target); // 디버그용 로그
    if (target.classList.contains('active')) {
        nextPage();
    } else if (target.classList.contains('flipped')) {
        prevPage();
    }
};

onMounted(() => {
    console.log('Component mounted'); // 디버그용 로그
});

onBeforeUnmount(() => {
    console.log('Component before unmount'); // 디버그용 로그
});
</script>

<style scoped>
/* Your existing styles can go here */


h1 {
    text-align: center;
}

.scene {
    width: 45%;
    height: 90%;
    margin: 2% 2% 2% 50%;
    perspective: 1000px;
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
    transition: 1.5s transform;
    transform-style: preserve-3d;
    transform-origin: left center;
}

.front,
.back {
    position: absolute;
    width: 100%;
    height: 100%;
    padding: 2% 5% 5%;
    box-sizing: border-box;
    backface-visibility: hidden;
    background: linear-gradient(to bottom right, #f5f5dc, #ffffff);
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
    text-indent: 1em;
    line-height: 1.7;
}

.qr {
    margin: 50px auto;
    max-width: 50%;
}

.qr img {
    display: block;
}
</style>
