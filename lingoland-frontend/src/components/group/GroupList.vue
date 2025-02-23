<script setup>
import defaultImage from "@/assets/sampleImg.jpg";
import { useGroupStore } from "@/stores/groups";
import { onMounted, ref, defineProps } from "vue";
import GroupListItem from "./GroupListItem.vue";
import { useRouter } from "vue-router";
import GroupJoinDialog from "./GroupJoinDialog.vue";

const { VITE_SERVER_IMAGE_URL } = import.meta.env;

const props = defineProps({
    checkMyGroup: Boolean,
});

const store = useGroupStore();
const router = useRouter();

const groupList = ref();
const dialog = ref({
    groupInfo: Object,
    isOpen: false,
});

function clickFunction(groupInfo, groupId) {
    // 내가 속한 그룹인 경우 그룹 디테일로 이동
    if (props.checkMyGroup) {
        router.push({
            name: "groupDetail",
            params: { groupId: groupId },
        });
    } else {
        // 내가 속한 그룹이 아닌 경우 가입 dialog 출력
        if (groupInfo.groupImage === "group/default.jpg") {
            groupInfo.groupImage = defaultImage;
        } else {
            groupInfo.groupImage = VITE_SERVER_IMAGE_URL + groupInfo.groupImage;
        }

        dialog.value.groupInfo = groupInfo;
        dialog.value.isOpen = true;
    }
}

onMounted(() => {
    let groupListPromise;

    if (props.checkMyGroup) {
        groupListPromise = store.getMyGroups();
    } else {
        groupListPromise = store.getGroups();
    }

    groupListPromise.then((promiseValue) => {
        groupList.value = promiseValue;
    });
});
</script>

<template>
    <v-expansion-panels class="d-flex pa-4 game-member-list" variant="popout">
        <v-expansion-panel
            v-for="(group, i) in groupList"
            :key="i"
            hide-actions
        >
            <GroupListItem
                :group="group"
                :check-my-group="props.checkMyGroup"
                @click-event="clickFunction(group, group.id)"
            />
        </v-expansion-panel>

        <GroupJoinDialog v-model="dialog" />
    </v-expansion-panels>
</template>

<style scoped>
.game-member-list {
    height: auto;
    max-height: 70%; /* 최대 높이 설정 */
    overflow-y: auto; /* 수직 스크롤을 가능하게 설정 */
}
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
    background: purple;
    border-radius: 10px;
}
</style>
