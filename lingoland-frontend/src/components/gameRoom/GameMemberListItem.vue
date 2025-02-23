<script setup>
import { useUserStore } from "@/stores/user";
import { defineProps } from "vue";

const props = defineProps({
    member: Object,
});

const userStore = useUserStore();
</script>

<template>
    <v-container class="border-md">
        <v-row rows="2">
            <h2 class="ml-4">{{ member.nickname }}</h2>
        </v-row>
        <v-row rows="10">
            <v-col cols="4">
                <img
                    :src="member.profileImage"
                    class="rounded-circle mx-auto"
                    style="height: 80px; width: 80px"
                />
            </v-col>
            <v-col cols="8">
                <h3 class="align-center">
                    {{ member.rank }}
                </h3>
                <v-progress-linear
                    rounded
                    height="25"
                    color="primary"
                    :model-value="member.experiencePoint"
                >
                    <template v-slot:default="{ value }">
                        <div class="text-button">{{ Math.ceil(value) }}%</div>
                    </template>
                </v-progress-linear>
            </v-col>
        </v-row>
    </v-container>
</template>

<style scoped>
.border-md {
    background-color: #fff1c2;
}
</style>
