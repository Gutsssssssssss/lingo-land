<script setup>
import { onMounted } from "vue";
import GroupMemberListItem from "./GroupMemberListItem.vue";

const props = defineProps({
    groupMemberList: Object,
    group: Object,
});
</script>

<template>
    <v-expansion-panels class="pa-4" variant="popout">
        <v-expansion-panel
            v-for="(groupMember, i) in groupMemberList"
            :key="i"
            hide-actions
        >
            <GroupMemberListItem :groupMember="groupMember" :group="group" />
        </v-expansion-panel>
    </v-expansion-panels>
</template>

<style scoped></style>
