<script setup>
import sampleImage from "@/assets/sampleImg.jpg";
import router from "@/router";
import { useGroupStore } from "@/stores/groups";
import { useUserStore } from "@/stores/user";
import { defineProps, onMounted } from "vue";
import { useRoute } from "vue-router";

const { VITE_SERVER_IMAGE_URL } = import.meta.env;

const route = useRoute();
const groupStore = useGroupStore();
const userStore = useUserStore();

const props = defineProps({
    groupMember: {
        type: Object,
        required: true,
    },
    group: Object,
});

onMounted(() => {
    // 이미지 파일명 확인 후 기본이미지 혹은 경로 추가 설정
    if (props.groupMember.profileImage === "member/default.jpg") {
        props.groupMember.profileImage = sampleImage;
    } else {
        props.groupMember.profileImage =
            VITE_SERVER_IMAGE_URL + props.groupMember.profileImage;
    }
    console.log(props.groupMember);
});

async function memberDetail() {
    try {
        const response = await userStore.getProfile();
        if (response.nickname === props.groupMember.nickname) {
            router.push({ name: "myPage" });
            return;
        }

        const isLeader = await groupStore.checkGroupLeader(
            route.params.groupId
        );
        if (isLeader) {
            console.log("byAdmin");
            router.push({
                name: "groupMemberDetailByAdmin",
                params: {
                    memberId: props.groupMember.loginId,
                    groupId: props.group.id,
                },
            });
        } else {
            console.log("justMember");
            router.push({
                name: "groupMemberDetail",
                params: {
                    memberId: props.groupMember.loginId,
                    groupId: props.group.id,
                },
            });
        }
    } catch (error) {
        console.error("An error occurred:", error);
    }
}
</script>

<template>
    <v-expansion-panel-title @click="memberDetail">
        <v-row class="spacer" no-gutters>
            <v-col cols="4" md="1" sm="2">
                <v-avatar size="36px">
                    <v-img :src="groupMember.profileImage"></v-img>
                </v-avatar>
            </v-col>
            <v-col cols="4" class="hidden-xs-only text-left ms-2" md="3" sm="5">
                <strong>{{ groupMember.nickname }}</strong>
            </v-col>

            <v-col cols="4" class="text-no-wrap text-left text-grey" sm="3">
                <strong>{{ groupMember.description }}</strong>
            </v-col>

            <v-spacer></v-spacer>
            <v-col
                v-if="groupMember.isLeader"
                class="text-no-wrap text-right text-grey"
                sm="auto"
            >
                <span class="material-symbols-outlined" style="color: #d1b041">
                    verified
                </span>
            </v-col>
        </v-row>
    </v-expansion-panel-title>

    <link
        rel="stylesheet"
        href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0"
    />
</template>

<style scoped></style>
