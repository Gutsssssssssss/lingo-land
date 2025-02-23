<script setup>
import GroupList from "@/components/group/GroupList.vue";
</script>

<template>
    <main class="mt-10">
        <div class="d-flex justify-center">
            <v-sheet rounded class="ma-10" width="1000">
                <v-row>
                    <h1 class="mt-7 mx-10">그룹 목록</h1>
                    <v-col class="mt-7 mb-0 pa-0">
                        <v-autocomplete
                            class="mx-auto"
                            density="comfortable"
                            menu-icon=""
                            placeholder="그룹 이름을 입력하세요."
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
                <GroupList :check-my-group="false" class="pa-0" />
            </v-sheet>
        </div>
    </main>
</template>
