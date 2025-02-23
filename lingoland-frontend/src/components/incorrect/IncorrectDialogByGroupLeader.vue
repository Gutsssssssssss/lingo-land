<script setup>
import PageNavigationButton from "@/components/common/PageNavigationButton.vue";
import IncorrectList from "@/components/incorrect/IncorrectList.vue";
import { defineProps, ref } from "vue";
import GenericButton from "../common/GenericButton.vue";
import NameTag from "../common/NameTag.vue";

const dialog = ref(false);
const props = defineProps({
    groupId: String,
    memberId: String,
});
</script>

<template>
    <PageNavigationButtonㄹ
        class="mt-5"
        background-color="#BF5847"
        data="오답노트"
        @click-event="
            () => {
                dialog = true;
            }
        "
        height="10vh"
    />
    <v-dialog v-model="dialog" width="50%" class="gowun-batang-regular">
        <NameTag data="오답노트" />
        <IncorrectList :group-id="groupId" :member-id="memberId" />

        <div class="d-flex justify-end bg-white">
            <GenericButton
                class="ma-3"
                @click-event="
                    () => {
                        dialog = false;
                    }
                "
                data="닫기"
                :style="{
                    width: '40px',
                    backgroundColor: '#4C4637',
                    color: 'white',
                }"
            />
        </div>
    </v-dialog>
</template>

<style>
</style>
