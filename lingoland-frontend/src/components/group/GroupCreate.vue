<script setup>
import imageSource from "@/assets/sampleImg.jpg";
import { useGroupStore } from "@/stores/groups";
import { storeToRefs } from "pinia";
import swal from "sweetalert2";
import { ref } from "vue";
import GenericButton from "../common/GenericButton.vue";
import GenericInput from "../common/GenericInput.vue";
import GenericInputArea from "../common/GenericInputArea.vue";
import ImageBox from "../common/ImageBox.vue";
import NameTag from "../common/NameTag.vue";
import SubmitButton from "../common/SubmitButton.vue";

window.Swal = swal;

const groupStore = useGroupStore();

const {
    groupNameFormat,
    passwordFormat,
    nameDuplicate,
    passwordCheck,
    groupDiscriptionFormat,
} = storeToRefs(groupStore);

const groupInfo = ref({
    name: "",
    password: "",
    description: "",
});

const checkedPassword = ref("");

function createGroup() {
    if (
        !groupNameFormat.value &&
        !nameDuplicate.value &&
        !passwordFormat.value &&
        !passwordCheck.value &&
        !groupDiscriptionFormat.value
    ) {
        groupStore.createGroup(groupInfo.value);
        console.log(groupNameFormat.value);
        console.log(nameDuplicate.value);
        console.log(passwordFormat.value);
        console.log(passwordCheck.value);
    } else {
        console.log(groupNameFormat.value);
        console.log(nameDuplicate.value);
        console.log(passwordFormat.value);
        console.log(passwordCheck.value);
        Swal.fire({
            title: "그룹 생성 양식을 확인해주세요",
            icon: "error",
        });
    }
}
</script>

<template>
    <v-main class="d-flex align-center justify-center">
        <div>
            <NameTag data="그룹 만들기" />

            <v-card width="1200">
                <!-- 하나의 행을 만듬 -->
                <v-row>
                    <v-col cols="6" class="d-flex align-center justify-center">
                        <ImageBox
                            :source="imageSource"
                            setting="align-center justify-center rounded-circle"
                        />
                    </v-col>

                    <v-col cols="6">
                        <div class="ma-10">
                            <v-row class="d-flex align-center justify-center">
                                <v-col cols="10">
                                    <GenericInput
                                        type="text"
                                        data="그룹명"
                                        id="name"
                                        v-model="groupInfo.name"
                                        @blur-event="
                                            groupStore.validateGroupNameFormat(
                                                groupInfo.name
                                            )
                                        "
                                    />
                                </v-col>
                                <v-col cols="2" class="px-0">
                                    <GenericButton
                                        data="중복확인"
                                        id="checkDuplicate"
                                        height="56"
                                        @click-event="groupStore.checkDuplicate(groupInfo.name)"
                                    />
                                </v-col>
                            </v-row>

                            <div v-if="groupNameFormat" class="text-red mb-3">
                                3~20자의 한글, 영문 소문자, 숫자와
                                특수기호(_),(-)만 사용 가능해요.
                            </div>

                            <GenericInputArea
                                data="그룹 소개"
                                v-model="groupInfo.description"
                                placeholder="그룹 소개를 입력하세요"
                                @blur-event="
                                    groupStore.validateGroupDiscriptionFormat(
                                        groupInfo.description
                                    )
                                "
                            />

                            <div
                                v-if="groupDiscriptionFormat"
                                class="text-red mb-3"
                            >
                                그룹 소개는 200자 이내로 작성 가능해요
                            </div>

                            <GenericInput
                                type="password"
                                data="비밀번호"
                                id="password"
                                hint="4자리 숫자를 입력해주세요"
                                v-model="groupInfo.password"
                                @blur-event="
                                    groupStore.validatePasswordFormat(
                                        groupInfo.password
                                    )
                                "
                            />

                            <div v-if="passwordFormat" class="text-red mb-3">
                                숫자 4자리만 사용가능해요.
                            </div>

                            <GenericInput
                                type="password"
                                data="비밀번호확인"
                                id="groupPasswordCheck"
                                v-model="checkedPassword"
                                @blur-event="
                                    groupStor.checkPassword(
                                        checkedPassword,
                                        groupInfo.password
                                    )
                                "
                            />

                            <div v-if="passwordCheck" class="text-red mb-3">
                                비밀번호가 같지 않아요
                            </div>

                            <SubmitButton
                                id="createGroup"
                                data="그룹 생성"
                                width="100%"
                                @click-event="createGroup"
                            />
                        </div>
                    </v-col>
                </v-row>
            </v-card>
        </div>
    </v-main>
</template>

<style scoped></style>
