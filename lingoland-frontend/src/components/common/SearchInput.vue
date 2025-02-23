<script setup>
import { defineModel, defineProps } from "vue";
/*
 id: 컴포넌트 id
 type: input 컴포넌트 type 지정
 data: input 컴포넌트 위에 지정할 이름 
*/
const model = defineModel();

const props = defineProps({
    id: String,
    type: String,
    data: String,
    hint: String,
});
</script>

<template>
    <div>
        <v-text-field
            clearable
            v-model="model"
            :type="type"
            :id="id"
            :placeholder="hint"
        ></v-text-field>
    </div>
</template>

<style scoped></style>
