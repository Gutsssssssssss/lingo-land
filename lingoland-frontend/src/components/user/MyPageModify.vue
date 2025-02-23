<script setup>
import sampleImage from "@/assets/sampleImg.jpg";
import { useUserStore } from "@/stores/user";
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import BackButton from "../common/BackButton.vue";
import GenericButton from "../common/GenericButton.vue";
import GenericInput from "../common/GenericInput.vue";
import ImageBox from "../common/ImageBox.vue";
import NameTag from "../common/NameTag.vue";
import SubmitButton from "../common/SubmitButton.vue";

const router = useRouter();

const userStore = useUserStore();
const changeNickName = ref("");

const userProfile = ref({
    nickname: "",
    profileImage: null,
    experiencePoint: 0,
});

const updateProfileImage = ref();

function modifyNickName() {
    // 닉네임 수정 API 요청
    userStore.modifyNickname(changeNickName.value);
}

function modifyProfileImage(event) {
    const file = event.target.files[0];

    if (file) {
        updateProfileImage.value = file;
        const reader = new FileReader();
        reader.readAsDataURL(file);

        userStore.modifyProfileImage(updateProfileImage.value);
    }
}

function modifyPassword() {
    router.push({ name: "myPageModifyPassword" });
}

onMounted(() => {
    const profile = userStore.getProfile();

    profile.then((getValue) => {
        userProfile.value.nickname = getValue.nickname;
        userProfile.value.experiencePoint = getValue.experiencePoint;

        changeNickName.value = getValue.nickname;

        if (getValue.profileImage === null) {
            userProfile.value.profileImage = sampleImage;
        } else {
            userProfile.value.profileImage = getValue.profileImage;
        }
    });
});
</script>

<template>
    <BackButton />
    <v-main class="d-flex align-center justify-center">
        <div>
            <NameTag data="프로필 수정" :style="{ width: '250px' }" />
            <v-card
                width="1200"
                height="500"
                class="d-flex align-center justify-center"
            >
                <v-row>
                    <v-col
                        cols="6"
                        class="d-flex flex-column align-center justify-center"
                    >
                        <ImageBox :source="userProfile.profileImage" />
                        <v-file-input
                            prepend-icon="mdi-camera"
                            hide-input
                            @change="modifyProfileImage"
                        ></v-file-input>
                    </v-col>

                    <v-col cols="6">
                        <div class="ma-10">
                            <v-row class="d-flex align-center justify-center">
                                <v-col cols="10">
                                    <GenericInput
                                        v-model="changeNickName"
                                        type="text"
                                        data="닉네임"
                                        id="userId"
                                        hint="변경할 닉네임을 입력하세요"
                                    />
                                </v-col>
                                <v-col cols="2" class="px-0">
                                    <GenericButton
                                        data="변경하기"
                                        id="modifyUserNickName"
                                        height="56"
                                        @click-event="modifyNickName"
                                    />
                                </v-col>
                            </v-row>

                            <SubmitButton
                                data="비밀번호 변경하러 가기"
                                width="100%"
                                @click-event="modifyPassword"
                            />
                        </div>
                    </v-col>
                </v-row>
            </v-card>
        </div>
    </v-main>
</template>

<style scoped></style>
