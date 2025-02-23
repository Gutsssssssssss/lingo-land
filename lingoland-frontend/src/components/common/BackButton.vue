<script setup>
import router from "@/router";

</script>

<template>
    <button
        class="d-flex justify-start"
        @click="
            () => {
                router.back();
            }
        "
        
    >
        <span class="material-symbols-outlined mt-4 ml-5"> arrow_back </span>
        <span class="mt-2 ml-1"></span>
    </button>
    <link
        rel="stylesheet"
        href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0"
    />
</template>

<style scoped>

button:hover{
    color : white;
}
</style>
