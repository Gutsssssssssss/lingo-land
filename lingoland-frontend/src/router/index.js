import GroupMemberDetail from "@/components/group/GroupMemberDetail.vue";
import GroupMemberDetailByAdmin from "@/components/group/GroupMemberDetailByAdmin.vue";
import MyPage from "@/components/user/MyPage.vue";
import { useUserStore } from "@/stores/user";
import GameRoomView from "@/views/GameRoomView.vue";
import GroupListView from "@/views/group/GroupListView.vue";
import GroupView from "@/views/group/GroupView.vue";
import LoginView from "@/views/LoginView.vue";
import MainPageView from "@/views/MainPageView.vue";
import MyPageView from "@/views/MyPageView.vue";
import SignUpView from "@/views/SignUpView.vue";
import { storeToRefs } from "pinia";
import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),

    routes: [
        {
            path: "/",
            redirect: "/login", // 기본 경로를 로그인 페이지로 리디렉션
        },

        {
            path: "/login",
            name: "login",
            component: LoginView,
            meta: {
                requiresAuth: false,
            },
        },
        {
            path: "/signup",
            name: "signUp",
            component: SignUpView,
            meta: {
                requiresAuth: false,
            },
        },
        {
            path: "/game-room",
            component: GameRoomView,
            meta: {
                requiresAuth: true,
            },
            children: [
                {
                    path: ":roomId",
                    name: "gameRoom",
                    component: () =>
                        import("@/components/gameRoom/TheGameRoom.vue"),
                },
                {
                    path: ":roomId/writing-game",
                    name: "writingGame",
                    component: () =>
                        import("@/views/writingGame/WritingGameView.vue"),
                },
                {
                    path: ":roomId/writing-game/result",
                    name: "writingGameResult",
                    component: () =>
                        import("@/views/writingGame/WritingGameResultView.vue"),
                },
                {
                    path: ":roomId/writing-game/loading",
                    name: "loading",
                    component: () => import("@/views/LoadingView.vue"),
                },
                {
                    path: ":roomId/running-game",
                    name: "runningGame",
                    component: () =>
                        import("@/views/runningGame/RunningGameView.vue"),
                },
                {
                    path: ":roomId/running-game/result",
                    name: "runningGameResult",
                    component: () =>
                        import("@/views/runningGame/RunningGameResultView.vue"),
                },
            ],
        },
        {
            path: "/main-page",
            name: "mainPage",
            component: MainPageView,
            meta: {
                requiresAuth: true,
            },
        },
        {
            path: "/my-page",
            component: MyPageView,
            meta: {
                requiresAuth: true,
            },
            children: [
                {
                    path: "",
                    name: "myPage",
                    component: MyPage,
                },
                {
                    path: "modify",
                    name: "myPageModify",
                    component: () =>
                        import("@/components/user/MyPageModify.vue"),
                },
                {
                    path: "modify/password",
                    name: "myPageModifyPassword",
                    component: () =>
                        import("@/components/user/PasswordModify.vue"),
                },
                {
                    path: "book",
                    name: "bookList",
                    component: () => import("@/views/tale/TaleListView.vue"),
                },
                {
                    path: "book/:bookId",
                    name: "bookDetail",
                    component: () => import("@/views/tale/TalesDetailView.vue"),
                },
            ],
        },
        {
            path: "/group",
            component: GroupView,
            meta: {
                requiresAuth: true,
            },
            children: [
                {
                    path: "list",
                    name: "groupList",
                    component: GroupListView,
                },
                {
                    path: "list/:groupId",
                    name: "groupDetail",
                    component: () =>
                        import("@/views/group/GroupDetailView.vue"),
                },
                {
                    path: "create",
                    name: "groupCreate",
                    component: () =>
                        import("@/components/group/GroupCreate.vue"),
                },
                {
                    path: ":groupId/modify",
                    name: "groupModify",
                    component: () =>
                        import("@/components/group/GroupModify.vue"),
                },
                {
                    path: ":groupId/member/:memberId",
                    name: "groupMemberDetail",
                    component: GroupMemberDetail,
                },
                {
                    path: ":groupId/member/:memberId/leader",
                    name: "groupMemberDetailByAdmin",
                    component: GroupMemberDetailByAdmin,
                    //그룹장만 입장 가능하게 해야 되맨!
                },
            ],
        },
    ],
});

// 라우팅 시 login 상태 확인
router.beforeEach(async (to, from) => {
    const metaByTo = to.meta;

    if (!metaByTo.requiresAuth) {
        return true;
    }

    const userStore = useUserStore();
    const { isAuthenticated } = storeToRefs(userStore);

    if (!isAuthenticated.value && to.name !== "login") {
        return { name: "login" };
    }
});

export default router;
