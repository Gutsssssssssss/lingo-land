import { modelConfiguration } from "@/configuration/modelConfiguration";
import { useOpenviduStore } from "@/stores/openvidu";
import { storeToRefs } from "pinia";
import Swal from "sweetalert2";
import * as THREE from "three";
import { ref } from "vue";
import { useGameStore } from "./gameStore";
import {
    checkAnswer,
    coinScore,
    coinTotalScore,
    reparticipants,
} from "./question";
import { useThreeJsStore } from "./threeStore";
import { countdown } from "./time";

const openviduStore = useOpenviduStore();
const { session } = openviduStore;
const { myCharacterIndex } = storeToRefs(openviduStore);

const gameStore = useGameStore();
const { isGameEnded, gameRanks, autoForwardSpeed } = storeToRefs(gameStore);

const threeJsStore = useThreeJsStore();
const { loader } = threeJsStore;

// const autoForwardSpeed = ref(0);
const selectedAnswerIndex = ref(null);
let moveSide = ref(0);
let chickModel;
let mixer;

const showRunningGameResult = () => {
    // 시그널 송신
    console.log(session.connection.connectionId);
    console.log("윤희의1시간", gameRanks);
    let lenPar = reparticipants.value.length;
    for (let i = 0; i < lenPar; i++) {
        if (
            gameRanks.value[i].connectionId === session.connection.connectionId
        ) {
            // console.log("지금의 기록", problemResult);
            // gameRanks.value[i].score += problemResult.score;
            gameRanks.value[i].score += coinScore.value;
            gameRanks.value[i].coin = coinTotalScore.value;
            coinScore.value = 0;
            break;
        }
    }
    session
        .signal({
            type: "gameEnd",
            data: JSON.stringify({ type: 1, data: "running game result" }),
        })
        .then(() => {
            console.log("***************************달리기 게임 결과화면 ㄱㄱ");
        })
        .catch((error) => {
            console.log("************ERROR 결과로 못가", error);
        });
};

console.log(
    "myCharacterIndex.value@@@@@@@@@@@@@@@@@@model",
    myCharacterIndex.value
);

function loadMyModel(scene, renderer, callback) {
    loader.load(
        modelConfiguration.models[myCharacterIndex.value].path,
        function (gltf) {
            gltf.scene.traverse((child) => {
                if (child.isMesh) {
                    child.material.needsUpdate = true;
                    if (child.material.map) {
                        child.material.map.anisotropy =
                            renderer.capabilities.getMaxAnisotropy();
                        child.material.map.needsUpdate = true;
                    }
                }
            });

            chickModel = gltf.scene; // 병아리 모델을 저장
            chickModel.scale.set(
                modelConfiguration.models[myCharacterIndex.value].scale,
                modelConfiguration.models[myCharacterIndex.value].scale,
                modelConfiguration.models[myCharacterIndex.value].scale
            ); // 크기 1배
            scene.add(chickModel);
            mixer = new THREE.AnimationMixer(chickModel);

            const action = mixer.clipAction(gltf.animations[0]);
            action.setLoop(THREE.LoopRepeat);
            action.play();

            chickModel.rotation.y =
                modelConfiguration.models[myCharacterIndex.value].rotation; // 정면을 보도록 설정 (0도로 설정)

            const chickBoundingBox = new THREE.Box3().setFromObject(chickModel);
            const chickHeight = chickBoundingBox.max.y - chickBoundingBox.min.y;
            if (modelConfiguration.models[myCharacterIndex.value].name == "dog") {
                chickModel.position.y = chickHeight / 3; // 맵 바닥 위로 위치 조정
                
            } else if (modelConfiguration.models[myCharacterIndex.value].name == "cat") {
                chickModel.position.y = chickHeight/2

            } 
            else{
            chickModel.position.y = chickHeight / 2; // 맵 바닥 위로 위치 조정
        }
            if (callback) callback(chickModel, mixer);
        }
    );
}

function handleChickMovement() {
    if (chickModel && countdown.value === 0 && !isGameEnded.value) {
        // 자동으로 z축을 따라 앞으로 이동
        chickModel.position.z += autoForwardSpeed.value; // 현재 속도 값에 따른 이동

        // 키 입력에 따른 옆으로 이동 처리
        if (moveSide.value !== 0) {
            const sideDirection = new THREE.Vector3(moveSide.value, 0, 0); // 옆 방향
            chickModel.position.add(sideDirection); // moveSide 값을 직접 사용
            moveSide.value = 0; // 한 번 이동한 후 상태 리셋
        }

        // x축 이동 범위 제한
        chickModel.position.x = Math.max(
            -4.0,
            Math.min(4.0, chickModel.position.x)
        );

        const { x, y, z } = chickModel.position;

        // x축 위치에 따른 정답 체크 및 하이라이트 처리
        if (x.toFixed(2) == -4.0) {
            selectedAnswerIndex.value = 2;
            checkAnswer(3);
        } else if (x.toFixed(2) == 0) {
            selectedAnswerIndex.value = 1;
            checkAnswer(2);
        } else if (x.toFixed(2) == 4.0) {
            selectedAnswerIndex.value = 0;
            checkAnswer(1);
        }

        // z 좌표가 9000이 되면 경기 종료
        // z 좌표가 9000이 되면 경기 종료
        if (z >= 7500) {
            gameStore.endGame(); // 경기 종료 함수 호출

            Swal.fire({
                title: "경기 종료!",
                icon: "success",
                timer: 5000,
            }).then(() => {
                chickModel.position.z = 0; // 모델의 위치 초기화
                showRunningGameResult();
                return
            });
        }
    }
}

function handleShoeMovement() {
    if (chickModel && countdown.value === 0 && !isGameEnded.value) {
        // 자동으로 z축을 따라 앞으로 이동
        chickModel.position.z += autoForwardSpeed.value;

        // 키 입력에 따른 옆으로 이동 처리
        if (moveSide.value !== 0) {
            const sideDirection = new THREE.Vector3(moveSide.value, 0, 0); // 옆 방향
            chickModel.position.add(sideDirection); // moveSide 값을 직접 사용
            moveSide.value = 0; // 한 번 이동한 후 상태 리셋
        }

        // x축 이동 범위 제한
        chickModel.position.x = Math.max(
            -4.0,
            Math.min(4.0, chickModel.position.x)
        );

        const { x, y, z } = chickModel.position;

        // x축 위치에 따른 정답 체크
        if (x.toFixed(2) == -4.0) {
            checkAnswer(3);
        } else if (x.toFixed(2) == 0) {
            checkAnswer(2);
        } else if (x.toFixed(2) == 4.0) {
            checkAnswer(1);
        }
    }
}

export {
    autoForwardSpeed,
    handleChickMovement,
    handleShoeMovement,
    loadMyModel,
    moveSide,
    selectedAnswerIndex,
};
