<script setup>
import GenericButton from "@/components/common/GenericButton.vue";
import PageNavigationButton from "@/components/common/PageNavigationButton.vue";
import Profile from "@/components/common/Profile.vue";
import incorrectDialog from "@/components/incorrect/IncorrectDialog.vue";
import router from "@/router";
import GroupList from "../group/GroupList.vue";
import MainPageButton from "../common/MainPageButton.vue";
import taleImg from "@/assets/mystory.png";

function clickTales() {
    router.push({ name: "bookList" });
}
</script>

<template>
    <MainPageButton />
    <v-main class="d-flex justify-center">
        <v-container>
            <v-row>
                <v-col cols="5" class="d-flex justify-center">
                    <Profile
                        @click-event="
                            () => {
                                router.push({ name: 'mainPage' });
                            }
                        "
                        :style="{ width: '80%' }"
                    />
                </v-col>

                <v-col cols="7">
                    <v-row>
                        <v-sheet
                            rounded
                            class="ma-3"
                            :style="{ backgroundColor: '#CCCBFF' }"
                            width="100%"
                            height="50dvh"
                        >
                            <v-row class="d-flex align-center justify-center">
                                <v-col cols="4">
                                    <h2
                                        class="mx-10"
                                        style="font-size: xx-large; width: 70%"
                                    >
                                        내 그룹 목록
                                    </h2>
                                </v-col>
                                <v-col cols="8">
                                    <div class="mr-5 mt-4">
                                        <v-autocomplete
                                            append-inner-icon="mdi-microphone"
                                            class="mx-auto"
                                            density="comfortable"
                                            menu-icon=""
                                            placeholder="그룹 이름을 입력하세요."
                                            prepend-inner-icon="mdi-magnify"
                                            style="max-width: 300px"
                                            theme="light"
                                            variant="solo"
                                            auto-select-first
                                            item-props
                                            rounded
                                        ></v-autocomplete>
                                    </div>
                                </v-col>
                            </v-row>
                            <GroupList :check-my-group="true" />
                        </v-sheet>
                    </v-row>
                    <v-row class="mr-1 d-flex justify-end">
                        <GenericButton
                            class="mr-3"
                            data="그룹 가입하기"
                            :style="{
                                backgroundColor: '#4C4637',
                                color: 'white',
                            }"
                            @click-event="$router.push({ name: 'groupList' })"
                        />
                        <GenericButton
                            data="그룹 만들기"
                            :style="{
                                backgroundColor: '#4C4637',
                                color: 'white',
                            }"
                            @click-event="$router.push({ name: 'groupCreate' })"
                        />
                    </v-row>
                    <v-row>
                        <v-col cols="6">
                            <incorrectDialog />
                        </v-col>

                        <v-col cols="6">
                            <PageNavigationButton
                                background-color="#537960"
                                data="ㅇㅇㅇ의 동화"
                                :source="taleImg"
                                @click-event="clickTales"
                                height="30vh"
                            />
                        </v-col>
                    </v-row>
                </v-col>
            </v-row>
        </v-container>
    </v-main>
</template>

<style scoped>
.room-code {
    background-color: #d2f0ff;
    width: 100%;
    height: 300px;
    border-radius: 4px;
    font-size: x-large;
}
</style>
