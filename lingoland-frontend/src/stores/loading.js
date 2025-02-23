import { ref, computed, inject } from "vue";
import { defineStore } from "pinia";
import swal from "sweetalert2";
import { useRouter } from "vue-router";
import { httpStatus } from "@/apis/http-status";

export const useLoadingStore = defineStore("loading", () => {
    const selectedtale = ref(null);
    // const tales = ref([]); //전체 talAes리스트
    // const tale = ref([]); // tale 하나에 들어있는 리스트
    const axios = inject("axios");
    const router = useRouter();
    window.Swal = swal;

    //마이페이지에서 클릭했을 때
    const myTalesList = async function () {
        // 내 tales list 불러오기
        const tales = await axios
            .get("/fairy-tales/members", { withCredentials: true })
            .then((response) => {
                if (response.status === httpStatus.OK) {
                    console.log(`length : ${response.data.length}`);
                    return response.data; // 명확하게 응답 데이터를 반환합니다.
                } else {
                    return []; // 예외 처리: 다른 상태 코드의 경우 빈 배열 반환
                }
            })
            .then((data) => {
                router.push({ name: "bookList" });
                return data; // data를 명확히 반환하여 tales 변수에 할당합니다.
            })
            .catch((error) => {
                console.error(error);
                return []; // 오류 발생 시 빈 배열 반환
            });

        return tales;
    };

    //다른 사람 프로필로 클릭했을 때
    const otherTalesList = async function (userId) {
        // 다른사람
        const tales = await axios
            .get(`/fairy-tales/members/${userId}`, { withCredentials: true })
            .then((response) => {
                if (response.status === httpStatus.OK) {
                    console.log(`length : ${response.data.length}`);
                    return response.data;
                }
            })
            .then((data) => {
                return data; // 여기에 data를 명확히 반환합니다.
            })
            .catch((error) => {
                console.error(error);
                return [];
            });

        return tales;
    };

    //동화 1개 내용전부 부르기
    const oneTaleById = async function (bookId) {
        const tale = await axios
            .get(`/fairy-tales/${bookId}`, { withCredentials: true })
            .then((response) => {
                if (response.status === httpStatus.OK) {
                    return response.data;
                }
            })
            .then((data) => {
                router.push({ name: "bookDetail", params: { bookId: bookId } });
                return data;
            });
        return tale;
    };

    return {
        oneTaleById,
        myTalesList,
        otherTalesList,
        selectedtale,
        // tales,
        // tale,
    };
});
