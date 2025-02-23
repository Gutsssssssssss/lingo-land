<script setup>
import { defineModel, defineProps } from "vue";

const emit = defineEmits(["blurEvent"]);

const model = defineModel();

const props = defineProps({
    data: String,
    placeholder: String,
});
</script>

<template>
    <div>
        {{ data }}
        <v-textarea
            v-model="model"
            variant="outlined"
            :placeholder="placeholder"
            @blur="$emit('blurEvent')"
            hide-details
        ></v-textarea>
    </div>
</template>

<style scoped></style>
