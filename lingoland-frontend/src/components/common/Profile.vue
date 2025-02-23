<script setup>
import { useUserStore } from "@/stores/user";
import { defineEmits, defineProps, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import ImageBox from "./ImageBox.vue";

const userStore = useUserStore();

const router = useRouter();
const route = useRoute();

const props = defineProps({
    others: Boolean,
    id: String,
    height: String,
});
defineEmits(["clickEvent"]);
const userProfile = ref({
    nickname: "",
    profileImage: null,
    experiencePoint: 0,
    rank: "",
});

function selectItem(event) {
    console.log(event);

    if (event === "로그아웃") {
        userStore.logout();
    } else if (event === "내 정보 수정") {
        // 내 정보를 가지고
        // 내 정보 수정 페이지로 이동
        router.push({ name: "myPageModify" });
    }
}

onMounted(() => {
    let profile;

    if (props.others) {
        profile = userStore.getProfileById(props.id);
        console.log("니 프로필");
    } else if (route.params.memberId) {
        profile = userStore.getProfileById(route.params.memberId);
        console.log("니 프로필");
    } else {
        profile = userStore.getProfile();
        console.log("내 프로필");
    }

    profile.then((getValue) => {
        userProfile.value.nickname = getValue.nickname;
        userProfile.value.experiencePoint = getValue.experiencePoint;
        userProfile.value.rank = getValue.rank;
        userProfile.value.profileImage = getValue.profileImage;
    });
});
</script>

<template>
    <v-card
        width="auto"
        height="auto"
        class="d-flex align-center"
        max-height="100vh"
    >
        <v-row class="d-flex flex-column pa-6">
            <v-col class="d-flex flex-column align-center justify-center">
                <v-select
                    v-if="!props.others"
                    class="fixed-select"
                    max-width="45"
                    density="compact"
                    variant="solo"
                    :items="['로그아웃', '내 정보 수정']"
                    @update:modelValue="selectItem"
                ></v-select>
                <ImageBox
                    :source="userProfile.profileImage"
                    @click="$emit('clickEvent')"
                />
                <div class="my-3" style="font-size: 5vw; font-weight: 700">
                    {{ userProfile.nickname }}
                </div>
                <div class="my-3" style="font-size: 2vw; font-weight: 700">
                    {{ userProfile.rank }}
                </div>
                <v-progress-linear
                    rounded
                    height="25"
                    color="primary"
                    :model-value="userProfile.experiencePoint"
                >
                    <template v-slot:default="{ value }">
                        <div class="text-button">{{ Math.ceil(value) }}%</div>
                    </template>
                </v-progress-linear>
            </v-col>
        </v-row>
    </v-card>
</template>

<style scoped>
.fixed-select {
    position: absolute;
    top: 15px;
    left: 15px;
}
</style>
