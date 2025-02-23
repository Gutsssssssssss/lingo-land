<script setup>
import router from "@/router";
</script>

<template>
    <button
        class="d-flex justify-start"
        @click="
            () => {
                router.push({ name: 'myPage' });
            }
        "
    >
        <span class="material-symbols-outlined mt-4 ml-5"> face </span>
    </button>
    <link
        rel="stylesheet"
        href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0"
    />
</template>

<style scoped>
button:hover {
    color: white;
}
</style>
