<script setup>
import PageNavigationButton from "@/components/common/PageNavigationButton.vue";
import IncorrectList from "@/components/incorrect/IncorrectList.vue";
import Swal from "sweetalert2";
import { ref } from "vue";
import GenericButton from "../common/GenericButton.vue";
import NameTag from "../common/NameTag.vue";
import incorrectImg from "@/assets/오답노트2.png"

const dialog = ref(false);

function completeProblem() {
    // 다이얼로그 닫기
    dialog.value = false;

    Swal.fire({
        title: "학습을 완료했어요!!!",
        icon: "success",
        confirmButtonText: "완료",
    }).then(() => {
        // 학습 완료 버튼을 누르면 다시 다이얼로그 열기
        dialog.value = true;
    });
}
</script>

<template>
    <PageNavigationButton
        background-color="#BF5847"
        data="오답노트"
        :source="incorrectImg"
        @click-event="
            () => {
                dialog = true;
            }
        "
        height="30vh"
    />
    <v-dialog v-model="dialog" width="50%" class="gowun-batang-regular">
        <NameTag data="오답노트" />
        <IncorrectList @click-event="completeProblem" />

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
