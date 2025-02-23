<script setup>
import { useUserStore } from "@/stores/user";
import { ref } from "vue";
import GenericInput from "../common/GenericInput.vue";
import SubmitButton from "../common/SubmitButton.vue";
import GameSlide from "./login/GameSlide.vue";
import NameTag from "../common/NameTag.vue";

const userStore = useUserStore();

userStore.$reset(); // 유저 상태 초기화

const warningMessage = ref("");

const loginInfo = ref({
    loginId: "",
    password: "",
});

const checkLoginId = ref(false);
const checkPassword = ref(false);

function login() {
    // 로그인 실행
    if (
        loginInfo.value.loginId.length !== 0 &&
        loginInfo.value.password.length !== 0
    ) {
        checkLoginId.value = false;
        checkPassword.value = false;
        userStore.login(loginInfo.value);

        // 아이디입력을 안했다.
    } else if (loginInfo.value.loginId.length === 0) {
        warningMessage.value = "아이디를 입력해주세요";
        checkLoginId.value = true;

        // 비밀번호 입력을 안했다.
    } else if (loginInfo.value.password.length === 0) {
        warningMessage.value = "비밀번호를 입력해주세요";
        checkPassword.value = true;
    }
}
</script>

<template>
    <v-main class="d-flex align-center justify-center ma-16">
        <v-row>
            <v-col cols="6" class="px-5">
                <NameTag data="로그인" />
                <v-card height="500">
                    <v-row>
                        <v-col>
                            <div class="ma-10">
                                <GenericInput
                                    v-model="loginInfo.loginId"
                                    type="text"
                                    data="아이디"
                                    id="loginId"
                                    @keyUp-event="login"
                                />

                                <GenericInput
                                    v-model="loginInfo.password"
                                    type="password"
                                    data="비밀번호"
                                    id="password"
                                    @keyUp-event="login"
                                />

                                <v-switch
                                    label="아이디저장"
                                    color="orange"
                                ></v-switch>

                                <div v-if="checkLoginId" class="text-red mb-3">
                                    {{ warningMessage }}
                                </div>
                                <div
                                    v-else-if="checkPassword"
                                    class="text-red mb-3"
                                >
                                    {{ warningMessage }}
                                </div>

                                <SubmitButton
                                    id="login"
                                    data="로그인"
                                    width="100%"
                                    @click-event="login"
                                />

                                <div class="my-5">
                                    <RouterLink :to="{ name: 'signUp' }"
                                        >회원가입</RouterLink
                                    >
                                </div>
                            </div>
                        </v-col>
                    </v-row>
                </v-card>
            </v-col>
            <v-col cols="6" class="px-5">
                <v-card class="mt-15">
                    <GameSlide />
                </v-card>
            </v-col>
        </v-row>
    </v-main>
</template>

<style scoped></style>
