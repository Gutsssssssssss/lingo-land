<script setup>
import WritingInput from "@/components/writingGame/WritingInput.vue";
import WritingList from "@/components/writingGame/WritingList.vue";
</script>

<template>
    <v-main class="background-image gowun-batang-regular pa-10">
        <v-row>
            <v-col
                cols="6"
                class="image d-flex flex-column justify-start align-start"
            >
                <WritingList />
            </v-col>

            <v-col
                cols="6"
                class="d-flex flex-column align-center"
                height="100vh"
            >
                <WritingInput />
            </v-col>
        </v-row>
    </v-main>
</template>

<style>
.background-image {
    background-image: url("@/assets/blackboard.png");
    background-size: cover;
    background-position: center 75%;
    background-repeat: no-repeat;
    height: 100vh;
    width: 100vw;
    background-color: black;
    overflow: hidden;
}

.image {
    background-image: url("/balloon.png");
    height: 95vh;
    width: 95vw;
    background-size: cover;
    background-position: center;
    position: relative;
    bottom: 25px;
    left: 35px;
}
</style>
