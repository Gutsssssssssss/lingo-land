<script setup>
import { useGroupStore } from "@/stores/groups";
import { ref } from "vue";
import GenericButton from "../common/GenericButton.vue";
import GenericInput from "../common/GenericInput.vue";
import GenericInputArea from "../common/GenericInputArea.vue";
import ImageBox from "../common/ImageBox.vue";
import NameTag from "../common/NameTag.vue";
import SubmitButton from "../common/SubmitButton.vue";

const groupStore = useGroupStore();
const model = defineModel();

const joinInfo = ref({
    description: "",
    password: "",
});

function joinGroup(groupId) {
    groupStore.joinGroup(groupId, joinInfo.value);
    model.value.isOpen = false;
}
</script>

<template>
    <v-dialog v-model="model.isOpen" width="850" height="600">
        <NameTag data="그룹 가입하기" />
        <v-sheet midth="850" height="600" class="d-flex align-center pa-15">
            <v-row class="mt-10">
                <v-col>
                    <v-row class="d-flex align-center justify-center">
                        <ImageBox :source="model.groupInfo.groupImage" />
                    </v-row>

                    <v-row class="d-flex align-center justify-center">
                        <h1>{{ model.groupInfo.name }}</h1>
                    </v-row>

                    <v-row class="d-flex mt-10 align-center justify-center">
                        <h3>{{ model.groupInfo.description }}</h3>
                    </v-row>
                </v-col>
                <v-col>
                    <h3>자기소개</h3>
                    <GenericInputArea v-model="joinInfo.description" />

                    <h3>비밀번호</h3>
                    <GenericInput type="password" v-model="joinInfo.password" />
                    <SubmitButton
                        class="ms-auto d-flex align-left"
                        data="가입하기"
                        width="100%"
                        @click-event="joinGroup(model.groupInfo.id)"
                    />
                    <GenericButton
                        class="mt-4 ms-auto d-flex align-left"
                        data="취소"
                        width="100%"
                        @click-event="
                            () => {
                                model.isOpen = false;
                            }
                        "
                    />
                </v-col>
            </v-row>
        </v-sheet>
    </v-dialog>
</template>

<style scoped></style>
