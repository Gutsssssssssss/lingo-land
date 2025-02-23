<script setup>
import { useGroupMemberStore } from "@/stores/groupMember";
import DropBox from "../common/DropBox.vue";
import RankListItem from "./RankListItem.vue";

const groupMemberStore = useGroupMemberStore();
</script>

<template>
    <div>
        <v-card color="rgba(172, 204, 124, 1)" class="pt-3 " height="90vh">
            <v-row>
                <v-col>
                    <h1 class="ml-10">순위</h1>
                </v-col>
                <v-col class="d-flex justify-end">
                    <DropBox width="50%" class="pr-8" />
                </v-col>
            </v-row>
            <v-expansion-panels class="d-flex pa-4 member-list" variant="popout">
                <v-expansion-panel
                    v-for="(member, i) in groupMemberStore.members"
                    :key="i"
                    hide-actions
                >
                    <!-- 아직 변수 값을 몰라 임의로 작성하였습니다. 데이터는 store에 임의로 작성하여 구성했습니다. -->
                    <RankListItem :member="member" />
                </v-expansion-panel>
            </v-expansion-panels>
        </v-card>
    </div>
</template>

<style scoped>
.member-list {
    height: auto;
    max-height: 70%; /* 최대 높이 설정 */
    overflow-y: auto;}
/* width */
::-webkit-scrollbar {
    width: 10px;
}

/* Track */
::-webkit-scrollbar-track {
    box-shadow: inset 0 0 5px grey;
    border-radius: 10px;
}

/* Handle */
::-webkit-scrollbar-thumb {
    background: rgb(252, 194, 85);
    border-radius: 10px;
}
</style>
