<script setup>
import { computed, defineEmits, defineProps } from "vue";

const props = defineProps({
    group: {
        type: Object,
        required: true,
    },
    checkMyGroup: Boolean,
});

const emit = defineEmits(["clickEvent"]);

const truncatedDescription = computed(() => {
    const maxLength = 20;
    return props.group.description.length > maxLength
        ? props.group.description.substring(0, maxLength) + "..."
        : props.group.description;
});
</script>

<template>
    <v-expansion-panel-title @click="$emit('clickEvent')">
        <v-row class="spacer" no-gutters>
            <v-col cols="12" sm="4" md="3" class="d-flex align-center">
                <div class="h6 font-weight-bold">
                    {{ group.name }}
                </div>
            </v-col>

            <v-col cols="12" sm="4" md="5" class="d-flex align-center">
                <div class="h6 font-weight-bold">
                    {{ truncatedDescription }}
                </div>
            </v-col>

            <v-col
                v-if="!checkMyGroup"
                cols="12"
                sm="2"
                md="2"
                class="d-flex align-center"
            >
                <div class="h6 font-weight-bold">
                    리더: {{ group.leaderNickname }}
                </div>
            </v-col>

            <v-col
                cols="12"
                sm="2"
                md="2"
                class="d-flex align-center justify-end"
            >
                <div class="h6 text-grey font-weight-bold">
                    그룹원 {{ group.memberCount }} 명
                </div>
            </v-col>
        </v-row>
    </v-expansion-panel-title>
</template>

<style scoped></style>
