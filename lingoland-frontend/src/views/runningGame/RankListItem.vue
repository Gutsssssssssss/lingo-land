<script setup>
import { defineProps } from "vue";

const props = defineProps({
    rank: {
        type: Object,
        required: true,
    },
});
</script>

<template>
    <v-expansion-panel-title>
        <v-row no-gutters>
            <v-col class="hidden-xs-only text-left ms-2">
                <strong>{{ rank.nickname }} ({{ rank.userId }})</strong>
            </v-col>
        </v-row>
    </v-expansion-panel-title>
</template>

<style scoped></style>
