<script setup>
import { useTaleStore } from "@/stores/tales";
import { defineProps } from "vue";
import DropBox from "../common/DropBox.vue";
import GenericButton from "../common/GenericButton.vue";
import router from "@/router";

const props = defineProps({
    tale: {
        type: Object,
        required: true,
    },
});
//id title cover summary

const taleStore = useTaleStore();

const clickTale = (bookId) => {
    taleStore.oneTaleById(bookId);
};
</script>

<template>
    <v-list-item-content>
        <v-row>
            <v-col cols="11" class="ma-5">
                <v-card variant="elevated">
                    <div class="d-flex flex-no-wrap">
                        <v-avatar class="ma-3" rounded="0" size="125">
                            <v-img :src="tale.cover"></v-img>
                        </v-avatar>

                        <div>
                            <v-card-title class="text-h5">
                                {{ tale.title }}
                            </v-card-title>

                            <v-card-text>
                                {{ tale.summary }}
                                ㅁㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ
                            </v-card-text>

                            <v-card-actions
                                class="d-flex justify-start align-center"
                            >
                                <GenericButton
                                    class="mr-5"
                                    data="소설 읽기"
                                    :style="{ backgroundColor: '#4C4637' }"
                                    @click-event="clickTale(tale.id)"
                                />
                                <DropBox
                                    class="d-flex align-center"
                                    width="300px"
                                    :item="['전체공개', '나만보기']"
                                    hint=""
                                />  
                            </v-card-actions>
                        </div>
                    </div>
                </v-card>
            </v-col>
        </v-row>
    </v-list-item-content>
</template>

<style scoped></style>
