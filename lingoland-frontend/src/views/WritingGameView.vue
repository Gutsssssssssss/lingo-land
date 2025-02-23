<script setup>
import WritingInput from "@/components/writingGame/WritingInput.vue";
import WritingList from "@/components/writingGame/WritingList.vue";
</script>

<template>
    <v-layout>
        <v-main
            class="background-image d-flex flex-column align-center justify-center"
        >
            <div class="text-h3 text-white my-16 pt-5">
                다음 글을 읽고 이어질 이야기를 작성하세요
            </div>

            <v-row class="d-flex justify-space-evenly">
                <v-col class="d-flex ma-10 pa-10 align-start">
                    <WritingList />
                </v-col>
                <v-col class="d-flex align-start ma-10 pa-5">
                    <WritingInput />
                </v-col>
            </v-row>
        </v-main>
    </v-layout>
</template>

<style scoped>
.background-image {
    background-image: url("@/assets/blackboard.png");
    background-size: contain;
    background-position: center;
    background-repeat: no-repeat;
    height: 100vh;
    width: 100%;
}
</style>
