<script setup>
import GenericInput from "../common/GenericInput.vue";
import SubmitButton from "../common/SubmitButton.vue";
</script>

<template>
    <GenericInput
        v-model="userInfo.loginId"
        type="text"
        data="아이디"
        id="loginId"
        @blur-event="validateIDFormat(userInfo.loginId)"
    />

    <GenericInput
        v-model="userInfo.password"
        type="password"
        data="비밀번호"
        id="password"
    />

    <v-switch label="아이디저장" color="orange"></v-switch>

    <SubmitButton id="login" data="로그인" width="100%" />
</template>

<style scoped></style>
