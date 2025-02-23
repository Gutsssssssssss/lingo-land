import { httpStatus } from "@/configuration/http-status";
import { defineStore } from "pinia";
import swal from "sweetalert2";
import { inject } from "vue";

export const useGameRoomStore = defineStore("gameRoom", () => {
    window.Swal = swal;

    const axios = inject("axios");

    // 세션을 만들고 방(세션)에 참가함
    const getSession = async () => {
        const sessionId = await axios
            .post("sessions", { withCredentials: true })
            .then((response) => {
                if (response.status === httpStatus.OK) {
                    console.log("**************getSession", response);
                    return Promise.resolve(response.data);
                }
            })
            .catch((error) => {
                console.log(error);
            });

        return sessionId;
    };

    // 방(세션)에 참가함
    const getToken = async (sessionId) => {
        const tokenId = await axios
            .post(`/sessions/${sessionId}/connections`, {
                withCredentials: true,
            })
            .then((response) => {
                if (response.status === httpStatus.OK) {
                    return Promise.resolve(response.data);
                }
            })
            .catch((error) => {
                console.log(error);
            });

        return tokenId;
    };

    return {
        getSession,
        getToken,
    };
});
