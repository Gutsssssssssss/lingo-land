import { httpStatus } from "@/configuration/http-status";
import { defineStore } from "pinia";
import { inject } from "vue";

export const useProblemStore = defineStore("problemStore", () => {
    /**
     * State
     */

    const axios = inject("axios");

    /**
     * actions
     */

    // 내 오답노트 가져오기
    const getMyWrongProblems = async () => {
        const myWrongProblemList = await axios
            .get(`/problems/wrong-problems`, { withCredentials: true })
            .then((response) => {
                if (response.status === httpStatus.OK) {
                    return Promise.resolve(response.data);
                }
            })
            .catch((error) => {
                console.log(error);
            });

        return myWrongProblemList;
    };

    const getWrongProblemsByadmin = async (groupId, memberId) => {
        const WrongProblemList = await axios
            .get(`/problems/wrong-problems/${groupId}/${memberId}`, {
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

        return WrongProblemList;
    };

    // 학습 완료된 오답노트 삭제하기
    const delectMyWrongProblem = async (problemId) => {
        const myWrongProblemList = await axios
            .delete(`/problems/wrong-problems/${problemId}`, {
                withCredentials: true,
            })
            .then((response) => {
                if (response.status === httpStatus.OK) {
                    return Promise.resolve(true);
                } else {
                    return Promise.resolve(false);
                }
            })
            .catch((error) => {
                console.log(error);
            });

        return myWrongProblemList;
    };

    return {
        getMyWrongProblems,
        delectMyWrongProblem,
        getWrongProblemsByadmin,
    };
});
