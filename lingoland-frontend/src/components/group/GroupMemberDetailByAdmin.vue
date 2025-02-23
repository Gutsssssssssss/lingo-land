<script setup>
import Profile from "@/components/common/Profile.vue";
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import IncorrectDialogByGroupLeader from "../incorrect/IncorrectDialogByGroupLeader.vue";
import TaleList from "../tale/TaleList.vue";
const route = useRoute();
const userId = ref(null);

onMounted(() => {
    userId.value = route.params.memberId;
    console.log("유저아이디", userId.value);
});
</script>

<template>
    <v-main v-if="userId" class="d-flex justify-center">
        <v-container>
            <v-row>
                <v-col cols="5">
                    <div>
                        <Profile class="mb-2" :others="true" :id="userId" />
                    </div>
                    <IncorrectDialogByGroupLeader
                        :group-id="route.params.groupId"
                        :member-id="userId"
                    />
                </v-col>

                <v-col cols="7">
                    <TaleList :others="true" :id="userId" :items-per-page=8 cols="3" :items-per-bar=4 :style="{width :'100%'}" />
                </v-col>
            </v-row>
        </v-container>
    </v-main>
</template>

<style scoped>
.room-code {
    background-color: #d2f0ff;
    width: 100%;
    height: 300px;
    border-radius: 4px;
    font-size: x-large;
}
</style>
