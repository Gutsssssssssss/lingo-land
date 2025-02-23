<script setup>
import GenericButton from "@/components/common/GenericButton.vue";
import GroupMemberList from "@/components/group/GroupMemberList.vue";
import router from "@/router";
import { useGroupMemberStore } from "@/stores/groupMember";
import { useGroupStore } from "@/stores/groups";
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();

const groupMemberStore = useGroupMemberStore();
const groupStore = useGroupStore();

const groupMemberList = ref();
const groupInfo = ref({
    name: "",
    id: "",
    description: "",
    memberCount: "",
    leaderNickname: "",
});

onMounted(async () => {
    const groupMemberPromise = groupMemberStore.getMyGroupMembers(
        route.params.groupId
    );

    groupMemberPromise.then(async (promise) => {
        groupMemberList.value = promise;
    });

    const groupPromise = groupStore.getGroup(route.params.groupId);

    groupPromise.then((promise) => {
        groupInfo.value = promise;
    });
});

function modify() {
    // 그룹장인 경우만 수정되도록
    groupStore.myGroup.value = groupInfo.value;
    router.push({ name: "groupModify" });
}
</script>

<template>
    <v-main class="d-flex mt-10 justify-center">
    
        <v-row>
            <v-col cols="2"> </v-col>
            <v-col cols="8">
                <v-sheet class="g-10">
                    <v-row>
                        <v-col cols="5">
                            <h1 class="mx-5 my-0">{{ groupInfo.name }}</h1>
                        </v-col>

                        <v-col>
                            <v-autocomplete
                                append-inner-icon="mdi-microphone"
                                class="mx-5 mt-1"
                                density="comfortable"
                                menu-icon=""
                                placeholder="이름을 입력하세요."
                                prepend-inner-icon="mdi-magnify"
                                style="max-width: 350px"
                                theme="light"
                                variant="solo"
                                auto-select-first
                                item-props
                                rounded
                            ></v-autocomplete>
                        </v-col>
                    </v-row>

                    <GroupMemberList :groupMemberList="groupMemberList" />

                    <v-row>
                        <v-col>
                            <div class="d-flex justify-end ma-2">
                                <GenericButton
                                    class="mx-5"
                                    data="그룹 수정"
                                    @click-event="modify"
                                />
                            </div>
                        </v-col>
                    </v-row>
                </v-sheet>
            </v-col>
        </v-row>
    </v-main>
</template>
