<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import CancelButton from "../common/CancelButton.vue";
import GenericInput from "../common/GenericInput.vue";
import NameTag from "../common/NameTag.vue";
import SubmitButton from "../common/SubmitButton.vue";
import { useUserStore } from "@/stores/user";
import BackButton from "../common/BackButton.vue";

const router = useRouter();
const userStore = useUserStore();

const currentPassword = ref("");
const passwordDTO = ref({
    password: "",
    checkedPassword: "",
});

const passwordFormat = ref(false);

// 비밀번호 포멧체크
function validatePasswordFormat(password) {
    // 비밀번호 최대길이 25

    const passwordRegex = /^[ㄱ-ㅎ가-힣a-z0-9_-]{6,20}$/;

    // 형식이 일치함
    if (passwordRegex.test(password)) {
        passwordFormat.value = false;
    } else {
        passwordFormat.value = true;
    }
}

function modify() {
    if (
        passwordDTO.value.password === passwordDTO.value.checkedPassword.value
    ) {
        Swal.fire({
            title: "비밀번호가 일치하지 않습니다.",
            icon: "error",
        });
    } else if (passwordFormat.value) {
        Swal.fire({
            title: "비밀번호 양식을 확인해주세요",
            icon: "error",
        });
    } else {
        userStore.modifyPassword(passwordDTO.value);
    }
}

function cancel() {
    console.log("비밀번호 변경 취소");
    router.push({ name: "myPageModify" });
}
</script>

<template>
    <BackButton />
    <v-main class="d-flex align-center justify-center">
        <div>
            <NameTag data="비밀번호 변경" :style="{ width: '220px' }" />

            <v-card class="d-flex align-center justify-center" width="600" height="550">
                <v-row>
                    <v-col class="ma-10 align-content-space-between">
                        <GenericInput
                            v-model="currentPassword"
                            type="password"
                            data="현재 비밀번호 "
                            id="userPassword"
                            hint="현재 비밀번호를 입력하세요"
                        />

                        <GenericInput
                            v-model="passwordDTO.password"
                            type="password"
                            data="변경할 비밀번호 "
                            id="newPassword"
                            hint="새 비밀번호"
                            @blur-event="
                                validatePasswordFormat(passwordDTO.password)
                            "
                        />

                        <div v-if="passwordFormat" class="text-red mb-3">
                            6~20자의 영문 소문자, 숫자와 특수기호(_),(-)만 사용
                            가능해요.
                        </div>

                        <GenericInput
                            v-model="passwordDTO.checkedPassword"
                            type="password"
                            data="변경할 비밀번호 확인"
                            id="newPasswordCheck"
                            hint="새 비밀번호 확인"
                        />

                        <SubmitButton
                            id="modify"
                            data="확인"
                            width="100%"
                            @click-event="modify"
                        />

                        <CancelButton
                            id="cancel"
                            data="취소"
                            width="100%"
                            @click-event="cancel"
                        />
                    </v-col>
                </v-row>
            </v-card>
        </div>
    </v-main>
</template>

<style scoped></style>
