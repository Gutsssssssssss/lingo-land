<script setup>
import { onMounted, ref, defineProps, computed } from "vue";
import { useTaleStore } from "@/stores/tales";
import { useRoute } from "vue-router";

const { VITE_SERVER_IMAGE_URL } = import.meta.env;

const route = useRoute();
const taleStore = useTaleStore();
const items = ref([]);

const search = ref("");
const currentPage = ref(1);
let itemsPerPage = 12; // 페이지당 항목 수
let cols = ref(2);
let itemsPerBar = ref(6);

const props = defineProps({
    others: Boolean,
    id: String,
    itemsPerPage: Number,
    cols: Number,
    itemsPerBar: Number,
});

const clickBook = (item) => {
    console.log("나는 눌리긴 하는중!@!@><");
    taleStore.oneTaleById(item.id);
};

onMounted(() => {
    if (props.itemsPerPage && props.cols && props.itemsPerBar) {
        itemsPerPage = props.itemsPerPage;
        cols.value = props.cols;
        itemsPerBar.value = props.itemsPerBar;
    }

    let talesListPromise;
    if (props.others) {
        console.log("다른 유저 동화");
        talesListPromise = taleStore.otherTalesList(props.id);
    } else if (route.params.memberId) {
        console.log("다른 유저 동화");
        talesListPromise = taleStore.otherTalesList(route.params.memberId);
    } else {
        console.log("내 동화");
        talesListPromise = taleStore.myTalesList();
    }

    talesListPromise.then((promiseValue) => {
        items.value = promiseValue;
    });
});

const filteredItems = computed(() => {
    if (!search.value) return items.value;
    return items.value.filter((item) =>
        item.title.toLowerCase().includes(search.value.toLowerCase())
    );
});

const paginatedItems = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    return filteredItems.value.slice(start, end);
});

const totalPages = computed(() => {
    return Math.ceil(filteredItems.value.length / itemsPerPage);
});

const onSearchInput = () => {
    currentPage.value = 1; // 검색 시 현재 페이지를 1로 초기화
};
</script>

<template>
    <v-container
        class="bg-color d-flex flex-column justify-center align-center"
    >
        <v-row
            class="d-flex justify-center align-center mt-10"
            style="position: relative; width: 95%"
        >
            <div
                v-if="paginatedItems.length > itemsPerBar"
                class="cube-face-bar1"
            ></div>
            <div
                v-if="paginatedItems.length > itemsPerBar"
                class="cube-face-bar2"
            ></div>
            <div
                v-if="paginatedItems.length <= itemsPerBar"
                class="cube-face-bar3"
            ></div>

            <v-col
                v-for="item in paginatedItems"
                :key="item.id"
                :cols="cols"
                @click="clickBook(item)"
                max-width="100px"
                style="perspective: 1000px"
                class="my-3"
            >
                <div
                    class="book-container"
                    style="
                        position: relative;
                        width: 100px;
                        /* height: 150px; */
                        transform-style: preserve-3d;
                        transform: rotateY(-30deg);
                        box-sizing: border-box;
                        perspective: 400px;
                    "
                >
                    <v-img
                        :src="VITE_SERVER_IMAGE_URL + item.cover"
                        height="100%"
                        class="image-shadow"
                        style="position: relative; top: 10px; right: 10px"
                    >
                    </v-img>

                    <div class="cube-face-right"></div>
                </div>
                <div class="gowun-batang-regular mt-4 truncated-text">
                    {{ item.title }}
                </div>
            </v-col>
        </v-row>
        <v-row
            class="d-flex align-center"
            style="position: fixed; bottom: 20px; right: 30px"
        >
            <v-col>
                <div class="d-flex justify-start">
                    <v-text-field
                        class="search"
                        v-model="search"
                        label="제목을 입력해 주세요"
                        prepend-inner-icon="mdi-magnify"
                        variant="solo-filled"
                        flat
                        hide-details
                        single-line
                        @input="onSearchInput"
                    ></v-text-field>
                </div>
            </v-col>
            <v-col>
                <v-pagination
                    class="pagination"
                    v-model="currentPage"
                    :length="totalPages"
                    color="primary"
                ></v-pagination>
            </v-col>
        </v-row>
    </v-container>
</template>

<style scoped>
.bg-color {
    background-color: white;
}

.truncated-text {
    white-space: nowrap; /* 텍스트를 한 줄로 유지 */
    overflow: hidden; /* 넘치는 텍스트 숨김 */
    text-overflow: ellipsis; /* 넘치는 부분을 ...으로 표시 */
    max-width: 140px; /* 필요에 따라 최대 너비 설정 */
    font-size: 10px;
}

.book-row {
    height: 500px;
    width: 70%;
    padding-bottom: 20px; /* 페이지네이션과의 간격 */
}

.pagination {
    width: 35vw;
    position: relative;
    left: 5%;
}

.search {
    width: 25vw;
    position: relative;
    left: 45%;
}

.gowun-batang-regular {
    font-family: "Gowun Batang", serif;
    font-weight: 500;
    font-style: normal;
    font-size: small;
}

.cube-face-right {
    background: rgb(255, 250, 226);
    border: 1px black;
    transform: rotateY(60deg) translateZ(20px);
    position: absolute;
    top: 10%;
    right: 8px;
    width: 15%;
    height: 94%;
    box-shadow: 5px 1px 15px rgba(0, 0, 0, 0.5); /* 그림자 효과 추가 */
}

.cube-face-bar1 {
    position: absolute;
    background: rgba(104, 63, 9, 0.3);
    border: 1px black;
    position: absolute;
    top: 38%;
    width: 94%;
    height: 1%;
    box-shadow: 5px 1px 15px rgba(0, 0, 0, 0.5);
}

.cube-face-bar2 {
    position: absolute;
    background: rgba(198, 197, 197, 0.8);
    border: 1px black;
    position: absolute;
    top: 88%;
    width: 94%;
    height: 1%;
    box-shadow: 5px 1px 15px rgba(0, 0, 0, 0.5); /* 그림자 효과 추가 */
}

.cube-face-bar3 {
    position: absolute;
    background: rgba(198, 197, 197, 0.8);
    border: 1px black;
    position: absolute;
    top: 76%;
    width: 94%;
    height: 2%;
    box-shadow: 5px 1px 15px rgba(0, 0, 0, 0.5); /* 그림자 효과 추가 */
}
</style>
