<script setup>
import makingRoomImg from "@/assets/animal/ANIMALS2.png";
import GenericButton from "@/components/common/GenericButton.vue";
import GenericInput from "@/components/common/GenericInput.vue";
import MyPageButton from "@/components/common/MyPageButton.vue";
import PageNavigationButton from "@/components/common/PageNavigationButton.vue";
import Profile from "@/components/common/Profile.vue";
import { useGameRoomStore } from "@/stores/gameRoom";
import { useOpenviduStore } from "@/stores/openvidu";
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const gameRoomStore = useGameRoomStore();
const openviduStore = useOpenviduStore();

const { OV, session } = openviduStore;
const roomCode = ref("");

function makeRoom() {
    const sessionPromise = gameRoomStore.getSession();

    sessionPromise.then((customToken) => {
        session
            .connect(customToken.token)
            .then(() => {
                const publisher = OV.initPublisher("publisher");
                session.publish(publisher);

                router.push({
                    name: "gameRoom",
                    params: { roomId: customToken.sessionId },
                });
            })
            .catch((error) => {
                console.error(
                    "There was an error connecting to the session:",
                    error
                );
            });
    });
}

function joinRoom() {
    gameRoomStore.getToken(roomCode.value).then((customToken) => {
        // 토큰을 얻어왔어
        session
            .connect(customToken.token)
            .then(() => {
                console.log("****my connectionId", session.connection);

                const publisher = OV.initPublisher("publisher");
                session.publish(publisher);

                router.push({
                    name: "gameRoom",
                    params: { roomId: roomCode.value },
                });
            })
            .catch((error) => {
                console.error(
                    "There was an error connecting to the session:",
                    error
                );
            });
    });
}
</script>

<template>
    <MyPageButton />
    <v-main class="d-flex justify-center">
        <v-container>
            <v-row>
                <v-col cols="5" class="d-flex justify-center">
                    <Profile
                        @click-event="
                            () => {
                                router.push({ name: 'myPage' });
                            }
                        "
                        :style="{ width: '80%' }"
                    />
                </v-col>

                <v-col cols="7" class="d-flex justify-center">
                    <v-row>
                        <v-col>
                            <PageNavigationButton
                                background-color="#CCCBFF"
                                data="방 만들기"
                                :source="makingRoomImg"
                                @click-event="makeRoom"
                                height="100%"
                            />
                        </v-col>

                        <v-col
                            class="room-code d-flex flex-column justify-center align-center ma-3"
                        >
                            <div style="font-size: xx-large; font-weight: 700">
                                방 코드
                            </div>
                            <GenericInput
                                v-model="roomCode"
                                :style="{ width: '70%' }"
                                @keyUp-event="joinRoom"
                            />
                            <GenericButton
                                data="입력"
                                :style="{ width: '70%' }"
                                @click-event="joinRoom"
                                class="mb-5"
                            />
                        </v-col>
                    </v-row>
                </v-col>
            </v-row>
        </v-container>
    </v-main>
</template>

<style scoped>
.room-code {
    background-color: #d2f0ff;
    border-radius: 4px;
    font-size: x-large;
}
</style>
