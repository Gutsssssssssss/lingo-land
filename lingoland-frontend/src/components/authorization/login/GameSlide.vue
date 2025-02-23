<script setup></script>

<template>
    <v-carousel
        class="pa-5"
        height="500"
        show-arrows="hover"
        cycle
        hide-delimiter-background
    >
        <v-carousel-item>
            <img
                src="\src\assets\runninggame.png"
                width="100%"
                height="80%"
                margin-bottom="10px"
            />
            <div>문해력 향상을 위한 달리기게임!</div>
        </v-carousel-item>

        <v-carousel-item>
            <img src="\src\assets\writinggame.png" width="100%" height="80%" />
            <div>릴레이로 글을 써서 동화를 만들어요!</div>
        </v-carousel-item>
    </v-carousel>
</template>
<script></script>

<style scoped></style>
