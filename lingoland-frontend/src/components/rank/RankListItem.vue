<script setup>
import { defineProps } from "vue";

const props = defineProps({
    member: {
        type: Object,
        required: true,
    },
});
</script>

<template>
    <v-expansion-panel-title>
        <v-row no-gutters>
            <v-col cols="4">
                <v-avatar size="36px">
                    <v-img
                        v-if="member.avatar"
                        alt="Avatar"
                        :src="member.avatar"
                    ></v-img>
                </v-avatar>
            </v-col>
            <v-col class="hidden-xs-only text-left ms-2">
                <strong>{{ member.name }}</strong>
            </v-col>

            <v-col class="text-no-wrap text-left text-grey">
                <strong>{{ member.rank }}</strong>
            </v-col>
        </v-row>
    </v-expansion-panel-title>
</template>

<style scoped></style>
