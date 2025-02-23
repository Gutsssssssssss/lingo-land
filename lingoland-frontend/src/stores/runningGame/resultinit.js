import { storeToRefs } from "pinia";
import * as THREE from "three";
import { OrbitControls } from "three/examples/jsm/controls/OrbitControls.js";
import { ref } from "vue";
import { useOpenviduStore } from "../openvidu";
import { useGameStore } from "./gameStore";
import { addLights } from "./light";
import { loadMapSection } from "./resultmap";
import { useThreeJsStore } from "./threeStore";

import { modelConfiguration } from "@/configuration/modelConfiguration";

const threeJsStore = useThreeJsStore();
const { loader } = threeJsStore;

// 카운트다운 & 타이머
// 문제
const openviduStore = useOpenviduStore();
const { reparticipants } = storeToRefs(openviduStore);

const gameStore = useGameStore();
const { sortedRanks } = storeToRefs(gameStore);

let renderer, scene, mixer, camera, controls, podiumModel;
let moveSide = ref(0);
const resultlst = ref([]);
let resultlen;

// 카메라 설정
const cameraSettings = {
    distance: 15, // 카메라와 캐릭터 사이의 거리
    height: 5, // 카메라의 높이
    angle: Math.PI / 6 + Math.PI / 6 + Math.PI / 6 + Math.PI / 6, // 카메라의 각도 (라디안) // 초기 각도
};

function setWinner() {
    resultlst.value = [];

    if (sortedRanks.value.length < 3) {
        resultlen = sortedRanks.value.length;
    } else {
        resultlen = 3;
    }

    for (let i = 0; i < resultlen; i++) {
        for (let j = 0; j < reparticipants.value.length; j++) {
            if (
                sortedRanks.value[i].connectionId ==
                reparticipants.value[j].connectionId
            ) {
                resultlst.value.push({
                    winnerInfo: modelConfiguration.models[j],
                });
                break;
            }
        }
    }
}

function initDraw() {
    setWinner();
    cameraSettings.distance=15
    cameraSettings.height=5
    cameraSettings.angle= Math.PI / 6 + Math.PI / 6 + Math.PI / 6 + Math.PI / 6 // 카메라의 각도 (라디안) // 초기 각도

    const canvas = document.querySelector("#c");

    scene = initScene();
    renderer = initRenderer(canvas);
    camera = initCamera();
    controls = initControls(camera, renderer);

    addLights(scene);

    loadMapSection(-100);
    loadpodiumModel();
    window.addEventListener("resize", onWindowResize, false);

    // 카메라 애니메이션 시작
    startCameraRotationAnimation();
    console.log(resultlst.value);
    console.log(resultlen);
    for (let k = 0; k < resultlen; k++) {
        console.log("헤헤 이게 위너정보입니답쇼@@@", resultlst.value[k]);
        loadWinners(resultlst.value[k].winnerInfo, k);
    }
    function animate() {
        requestAnimationFrame(animate);
        const delta = clock.getDelta(); // delta time 계산
        if (mixer) mixer.update(delta); // delta를 이용한 애니메이션 업데이트
        controls.update(); // 마우스 조작 업데이트

        renderer.render(scene, camera);
    }

    animate();
}

function loadWinners(modelInfo, index) {
    loader.load(modelInfo.path, function (gltf) {
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

        let winnerModel = gltf.scene; // 병아리 모델을 저장
        winnerModel.scale.set(
            modelInfo.scale,
            modelInfo.scale,
            modelInfo.scale
        ); // 크기 1배
        scene.add(winnerModel);
        mixer = new THREE.AnimationMixer(winnerModel);
        const action = mixer.clipAction(gltf.animations[0]);
        action.play();
        winnerModel.rotation.y = Math.PI / 3 + modelInfo.rotation; // 90도 회전 (왼쪽을 보던 방향을 정면으로 조정)

        const podiumBoundingBox = new THREE.Box3().setFromObject(podiumModel);

        if (index === 0) {
            winnerModel.position.y = podiumBoundingBox.max.y + 0.5;
            winnerModel.position.z -= 5; // 맵 바닥 위로 위치 조정
            winnerModel.position.x += 4;
        } else if (index == 1) {
            winnerModel.position.y = podiumBoundingBox.max.y + 0.5 - 1.05;
            winnerModel.position.z += -3.5 - 1 * 2 * Math.sin(Math.PI / 3) + 4; // 맵 바닥 위로 위치 조정
            winnerModel.position.x += 2.8 + -1 * 2 * Math.cos(Math.PI / 3);
        } else {
            winnerModel.position.y = podiumBoundingBox.max.y + 0.5 - 1.7;
            winnerModel.position.z += -10.7 - 1 * 2 * Math.sin(Math.PI / 3) + 4; // 맵 바닥 위로 위치 조정
            winnerModel.position.x += 7 + -1 * 2 * Math.cos(Math.PI / 3);
        }
    });
}

function loadpodiumModel() {
    loader.load("/winner_podium/scene.gltf", function (gltf) {
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

        podiumModel = gltf.scene; // 병아리 모델을 저장
        podiumModel.scale.set(2, 2, 2); // 크기 1배
        scene.add(podiumModel);

        podiumModel.rotation.y = Math.PI / 3 + Math.PI / 3 + Math.PI / 6; // 90도 회전 (왼쪽을 보던 방향을 정면으로 조정)

        const podiumBoundingBox = new THREE.Box3().setFromObject(podiumModel);
        const podiumHeight = podiumBoundingBox.max.y - podiumBoundingBox.min.y;
        podiumModel.position.y = 0;
        podiumModel.position.z -= 5; // 맵 바닥 위로 위치 조정
        podiumModel.position.x += 4;
    });
}
function initScene() {
    const scene = new THREE.Scene();
    scene.background = new THREE.Color(0x87ceeb); // 하늘색 배경 설정
    return scene;
}

function initRenderer(canvas) {
    const renderer = new THREE.WebGLRenderer({
        canvas: canvas,
        antialias: true,
    });
    renderer.outputEncoding = THREE.sRGBEncoding;
    renderer.localClippingEnabled = true; // 클리핑 평면 사용 활성화
    renderer.setPixelRatio(window.devicePixelRatio);
    renderer.setSize(window.innerWidth, window.innerHeight);
    return renderer;
}

function initCamera() {
    const camera = new THREE.PerspectiveCamera(
        75,
        window.innerWidth / window.innerHeight,
        0.5,
        1000
    );
    // 90도 회전 (왼쪽을 보던 방향을 정면으로 조정)
    updateCameraPosition();

    // 초기 카메라 위치 설정

    // 시점을 카메라 높이보다 약간 높은 위치로 설정
    camera.lookAt(0, cameraSettings.height + 100, 0);

    return camera;
}

function initControls(camera, renderer) {
    const controls = new OrbitControls(camera, renderer.domElement);
    controls.enableDamping = true; // 부드러운 움직임을 위해 감속 효과 추가
    controls.dampingFactor = 0.25;
    controls.screenSpacePanning = false;
    controls.maxPolarAngle = Math.PI / 2;
    controls.enableZoom = false; // 줌 기능 비활성화
    return controls;
}
function updateCameraPosition() {
    if (podiumModel) {
        const podiumPosition = podiumModel.position.clone();
        const podiumRotation = podiumModel.rotation.y;

        const cameraOffset = new THREE.Vector3(
            cameraSettings.distance * Math.sin(cameraSettings.angle),
            cameraSettings.height,
            cameraSettings.distance * Math.cos(cameraSettings.angle)
        );

        cameraOffset.applyMatrix4(
            new THREE.Matrix4().makeRotationY(podiumRotation)
        );
        camera.position.set(
            5,
            podiumPosition.y + cameraOffset.y,
            podiumPosition.z + cameraOffset.z
        ); // x축을 0으로 설정
        camera.lookAt(0, podiumPosition.y + 100, podiumPosition.z); // 카메라가 병아리 모델을 항상 바라보도록 설정
    }
}

function onWindowResize() {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(window.innerWidth, window.innerHeight);
}

function startCameraRotationAnimation() {
    const rotationDuration = 4000; // 4초 동안 애니메이션
    const rotationAngle = Math.PI / 4; // 45도 회전
    const startAngle = cameraSettings.angle;
    const startTime = performance.now(); // 애니메이션 시작 시간

    function animateRotation() {
        const currentTime = performance.now();
        const elapsedTime = currentTime - startTime;
        const progress = Math.min(elapsedTime / rotationDuration, 1); // 애니메이션 진행 상태 (0에서 1로)

        // 현재 각도 계산
        const currentAngle = startAngle - progress * rotationAngle;

        // 카메라 위치 업데이트
        camera.position.set(
            cameraSettings.distance * Math.sin(currentAngle),
            cameraSettings.height,
            cameraSettings.distance * Math.cos(currentAngle)
        );

        // 카메라의 시점을 조정합니다.
        camera.lookAt(0, camera.position.y, 0);

        if (progress < 1) {
            requestAnimationFrame(animateRotation); // 애니메이션 계속 진행
        } else {
            // 최종 각도 설정
            cameraSettings.angle = currentAngle;
        }
    }

    animateRotation(); // 애니메이션 시작
}

const clock = new THREE.Clock();

export {
    camera,
    cameraSettings,
    controls,
    initDraw,
    mixer,
    moveSide,
    podiumModel,
    renderer,
    scene,
    startCameraRotationAnimation,
    updateCameraPosition
};

