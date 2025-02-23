import sampleImg from "@/assets/sampleImg.jpg";
import { OpenVidu } from "openvidu-browser";
import { defineStore, storeToRefs } from "pinia";
import { inject, ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "./user";
import { useWritingGameStore } from "./writingGame";

export const useOpenviduStore = defineStore("openvidu", () => {
    const axios = inject("axios");

    const OV = new OpenVidu();
    const session = OV.initSession();

    const router = useRouter();
    const userStore = useUserStore();
    const participants = ref([]);
    const myCharacterIndex = ref();

    const reparticipants = ref([]);
    const storyList = ref([]);
    const storyOrderList = ref([]);

    const writingGameStore = useWritingGameStore();
    const { turn, pageCount } = storeToRefs(writingGameStore);

    // 세션에 스트림이 생성될 때 호출되는 콜백 함수
    session.on("streamCreated", function (event) {
        session.subscribe(event.stream, "subscriber");
    });

    // 세션에 새로운 유저가 참가하면 호출되는 콜백함수
    session.on("connectionCreated", (event) => {
        userStore.getProfileById(event.connection.data).then((info) => {
            // 참가자 배열에 이미 있는 사람인지 확인 필요
            // 배열에서 userId 중복되는지 확인
            const exists = participants.value.some(
                (participant) => participant.userId === event.connection.data
            );

            // 중복되지 않으면 배열에 추가
            if (!exists) {
                // 프로필 이미지가 없으면 기본 이미지를 넣어준다.
                if (!info.profileImage) {
                    info.profileImage = sampleImg;
                }

                participants.value.push({
                    connectionId: event.connection.connectionId,
                    userId: event.connection.data,
                    userProfile: info,
                    role:
                        event.connection.role == "MODERATOR"
                            ? event.connection.role
                            : "PUBLISHER",
                });
            }
        });
    });

    // 세션에 유저가 나가면 호출되는 콜백함수
    session.on("connectionDestroyed", (event) => {
        const connectionId = event.connection.connectionId;

        console.log(event);

        // 로그인 아이디 exitLoginId , 현재 몇 턴인지(order)
        // const exitUserDTO = ref({
        //     exitLoginId: "",
        //     order: turn.value,
        // });

        participants.value = participants.value.filter(
            (participant) => participant.connectionId !== connectionId
        );

        // 세션에 유저가 나가면 서버에게 API 요청을 보냄
        // removeConnection(event.sessionId, exitUserDTO);
    });

    // 게임 시작 Signal 수신 처리
    session.on("signal:gameStart", function (event) {
        const gameType = JSON.parse(event.data);

        // 참가자 커넥션 아이디를 기준으로 정렬
        reparticipants.value = participants.value.sort((a, b) => {
            return a.connectionId.localeCompare(b.connectionId);
        });

        // 달리기 게임으로
        if (gameType.type === 1) {
            for (let i = 0; i < reparticipants.value.length; i++) {
                if (
                    reparticipants.value[i].connectionId ==
                    event.target.connection.connectionId
                ) {
                    myCharacterIndex.value = i;
                    break;
                }
            }
            router.replace({ name: "runningGame" });

            // 글쓰기 게임으로
        } else if (gameType.type === 2) {
            // 방장이 보내준 글쓰기 페이지로 초기화
            pageCount.value = gameType.data;

            storyList.value = [];

            writingGameStore.setWritingGameState();

            // 이야기 배열 초기화
            initalizeStoryList();

            // 정렬된 참가자 아이디를 기준으로 이야기 작성 순서 초기화
            initalizeOrderList();

            // 글쓰기 게임으로
            router.replace({ name: "writingGame" });
        }
    });

    // 게임 종료 signal 수신 처리
    session.on("signal:gameEnd", async function (event) {
        const resultType = JSON.parse(event.data);
        if (resultType.type === 1) {
            router.push({ name: "runningGameResult" });
        } else if (resultType.type === 2) {
            // router.push({ name: "writingGameResult" });
            router.push({ name: "loading" });
        }
    });

    // 글쓰기 게임 제출 시그널 수신처리
    session.on("signal:writingGame", async function (event) {
        const writingGameSignal = await JSON.parse(event.data);

        await setStory(writingGameSignal);

        console.log("************storyList", storyList.value);
    });

    // 글쓰기 게임 턴 변경 시그널 수신 처리
    session.on("signal:turnOver", async function (event) {
        console.log("*****************턴 오버 시그널을 받았다", event);
        turn.value++;

        // 글쓰기 턴과 설정 페이지가 같으면 글쓰기 게임 종료
        if (turn.value == pageCount.value) {
            session
                .signal({
                    type: "gameEnd",
                    data: JSON.stringify({ type: 2 }),
                })
                .then(() => {
                    console.log("***********************writingGame end");
                })
                .catch((error) => {
                    console.error(
                        "******************error sending signal",
                        error
                    );
                });
        }
    });

    // 방 참가자 리스트 초기화
    function resetParticipants() {
        participants.value = [];
    }

    // 방장 확인
    function isLeader() {
        const isLeader = participants.value.some(
            (participant) => participant.role === "MODERATOR"
        );

        return isLeader;
    }

    // 이야기 배열 초기화
    function initalizeStoryList() {
        for (let index = 0; index < reparticipants.value.length; index++) {
            storyList.value.push({
                storyId: reparticipants.value[index].userId,
                story: [],
            });
        }
    }

    // 이야기 작성 배열 초기화
    function initalizeOrderList() {
        // 정렬된 참가자 순서로 이야기 순서 만들기
        for (let index = 0; index < reparticipants.value.length; index++) {
            let orderList = [];

            // 페이지 수만큼 반복
            // 이야기 아이디는 로그인 아이디를 기준으로 만듦
            for (
                let orderIndex = index;
                orderIndex < Number(index) + Number(pageCount.value);
                orderIndex++
            ) {
                orderList.push(
                    reparticipants.value[
                        orderIndex % reparticipants.value.length
                    ].userId
                );
            }

            storyOrderList.value.push({
                connectionId: reparticipants.value[index].connectionId,
                nickname: reparticipants.value[index].userId,
                orderList: orderList,
            });
        }

        console.log("**********storyOrderList", storyOrderList.value);
    }

    // 글쓰기 제출 시그널을 받았을 때 처리
    function setStory(writingGameSignal) {
        // 받은 이벤트로부터 스토리 아이디를 찾고
        // 스토리 리스트에서 해당하는 스토리 아이디에 이야기를 추가한다.
        for (let index = 0; index < storyList.value.length; index++) {
            if (storyList.value[index].storyId === writingGameSignal.storyId) {
                storyList.value[index].story.push(writingGameSignal.story);
                return;
            }
        }
    }

    // 현재 내가 작성하고 있는 이야기 아이디 찾기
    function findCurrentStoryId() {
        let currentStoryId = null;
        // storyOrderList에서 내 커넥션 아이디를 찾고
        // 현재 턴에 해당하는 이야기아이디를 찾음
        for (let index = 0; index < storyOrderList.value.length; index++) {
            if (
                storyOrderList.value[index].connectionId ===
                session.connection.connectionId
            ) {
                currentStoryId =
                    storyOrderList.value[index].orderList[turn.value];
                return currentStoryId;
            }
        }
    }

    // 내 로그인 아이디 찾기
    function findMyLoginId() {
        let myLoginId = null;
        for (let index = 0; index < participants.value.length; index++) {
            if (
                participants.value[index].connectionId ===
                session.connection.connectionId
            ) {
                myLoginId = participants.value[index].userId;
                return myLoginId;
            }
        }
    }

    // 세션에서 나간 사람이 있을 시 API요청
    // TODO: 백엔드 스펙나오면 개발진행
    const removeConnection = async (sessionId, exitUser) => {
        await axios
            .post(`/writing-game/exit/${sessionId}`, exitUser, {
                withCredentials: true,
            })
            .then((response) => {
                console.log(response);
            })
            .catch((error) => {
                console.log(error);
            });
    };

    return {
        OV,
        session,
        participants,
        reparticipants,
        myCharacterIndex,
        storyList,
        storyOrderList,
        resetParticipants,
        isLeader,
        findCurrentStoryId,
        findMyLoginId,
    };
});
