import { inject } from "vue";
import { defineStore } from "pinia";

export const useGroupMemberStore = defineStore("groupMember", () => {
    /**
     * State
     */

    const axios = inject("axios");

    const members = [
        {
            avatar: "https://avatars0.githubusercontent.com/u/9064066?v=4&s=460",
            name: "김초딩",
            rank: "정1품",
            description: "안녕하세요. 반갑습니다",
        },
        {
            avatar: "https://avatars0.githubusercontent.com/u/9064066?v=4&s=460",
            name: "이초딩",
            rank: "종6품",
            description: "안녕하세요. 반갑습니다",
        },
        {
            avatar: "https://avatars0.githubusercontent.com/u/9064066?v=4&s=460",
            name: "박초딩",
            rank: "정3품",
            description: "안녕하세요. 반갑습니다",
        },
        {
            avatar: "https://avatars0.githubusercontent.com/u/9064066?v=4&s=460",
            name: "최초딩",
            rank: "정1품",
            description: "안녕하세요. 반갑습니다",
        },
    ];

    /**
     * actions
     */
    const getMyGroupMembers = async (groupId) => {
        const myGroupMemberList = await axios
            .get(`/groups/${groupId}/users`)
            .then((response) => {
                return Promise.resolve(response.data);
            })
            .catch((error) => {
                console.log(error);
            });

        return myGroupMemberList;
    };

    return { members, getMyGroupMembers };
});
