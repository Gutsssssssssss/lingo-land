// import './assets/main.css'
import { createPinia } from "pinia";
import { createApp } from "vue";

import App from "./App.vue";
import router from "./router";

// Vuetify
import "@mdi/font/css/materialdesignicons.css"; // mdi icon 추가
import "material-design-icons-iconfont/dist/material-design-icons.css";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";
import { fa } from "vuetify/iconsets/fa";
import { aliases, mdi } from "vuetify/lib/iconsets/mdi";
import "vuetify/styles";

import { instance } from "@/apis/axios";

import { cloneDeep } from "lodash";

const vuetify = createVuetify({
    icons: {
        defaultSet: "mdi",
        aliases,
        sets: {
            fa,
            mdi,
        },
    },
    components,
    directives,
});

// 피니아 상태 초기화
function resetStore({ store }) {
    // store의 기본값을 deepClone
    const initalState = cloneDeep(store.$state);

    // deepClone한 상태로 재 설정
    store.$reset = () => store.$patch(cloneDeep(initalState));
}

const app = createApp(App);
const pinia = createPinia();
pinia.use(resetStore);

app.provide("axios", instance);
app.use(vuetify);
app.use(pinia);
app.use(router);

app.mount("#app");
