import { storeToRefs } from "pinia";
import * as THREE from "three";
import { OrbitControls } from "three/examples/jsm/controls/OrbitControls.js";
import { useGameStore } from "./gameStore";
import { addLights } from "./light";
import { loadMapSection } from "./map";

import {
    autoForwardSpeed,
    handleChickMovement,
    handleShoeMovement,
    loadMyModel,
    moveSide,
} from "./model";

import { countdown, startCountdown, updateTimer } from "./time";

import { useThreeJsStore } from "./threeStore";

let renderer,
    scene,
    mixer,
    camera,
    controls,
    chickModel,
    coinSound,
    shoeSound,
    soundbg;

const gameStore = useGameStore();
const { isGameEnded, coinScore, coinTotalScore, startfunc, start, startTime } =
    storeToRefs(gameStore);

const threeJsStore = useThreeJsStore();
const { loader, audioLoader, listener } = threeJsStore;

// 카메라 설정
const cameraSettings = {
    distance: 10, // 카메라와 캐릭터 사이의 거리
    height: 5, // 카메라의 높이
    angle: -Math.PI / 2, // 카메라의 각도 (라디안)
};

let keysPressed = setupKeyListeners();

function initDraw() {
    const canvas = document.querySelector("#c");

    scene = initScene();
    renderer = initRenderer(canvas);
    camera = initCamera();
    controls = initControls(camera, renderer);

    addLights(scene); // 조명 설정

    // 맵 설정
    for (let i = -1; i < 5; i++) {
        loadMapSection(i * 2040 - 680);
        loadMapSection(i * 2040 + 340);
    }

    // 나한테 해당하는 모델 불러오기
    loadMyModel(scene, renderer, (model, animMixer) => {
        chickModel = model;
        mixer = animMixer;
        startCameraAnimation(); // 모델이 로드된 후 카메라 애니메이션 시작
    });

    autoForwardSpeed.value = 0.5;
    threeJsStore.setAudio(camera); // 오디오 리스너 세팅
    loadAndPlayBackgroundMusic(camera); // 음악 재생 함수 호출
    loadCoinSound(camera); // 코인 사운드 로드
    loadShoeSound(camera); // 신발 아이템 사운드 로드

    initCoinModels(); // 코인 모델 초기화
    initShoeModels(); // 신발 아이템 로드

    // let startTime = Date.now(); // 타이머 시작 시간
    window.addEventListener("resize", onWindowResize, false); // 창 화면 크기에 따라서 화면 정렬

    function animate() {
        requestAnimationFrame(animate);
        // console.log(chickModel)
        const delta = clock.getDelta(); // delta time 계산

        if (mixer) mixer.update(delta); // delta를 이용한 애니메이션 업데이트
        controls.update(); // 마우스 조작 업데이트

        if (!isGameEnded.value) {
            if (isCameraAnimationDone) {
                if (!startfunc.value) {
                    startCountdown();
                    startfunc.value = true;
                }
                if (countdown.value == 0) {
                    if (!start.value) {
                        startTime.value = Date.now();
                        start.value = true;
                        updateTimer(startTime.value);
                    } else {
                        updateTimer(startTime.value);
                    }
                }

                handleChickMovement(); // 모델 움직임
                checkForCoinCollisions(); // 코인 충돌 감지 및 처리
                checkForShoeCollisions(); // 신발 충돌 감지 및 처리
                handleShoeMovement(); // 신발 먹으면 빨라지기
                updateCameraPosition(); // 카메라가 모델을 쫒아오도록 처리
            } else {
                updateCameraDuringAnimation(); // 시작할 때 모델 회전 카메라 무빙
            }
        }

        renderer.render(scene, camera);
    }

    animate();
}

function initScene() {
    const scene = new THREE.Scene();
    scene.background = new THREE.Color(0x87ceeb); // 하늘색 배경 설정
    return scene;
}

function startCameraAnimation() {
    camera.position.set(
        chickModel.position.x + 3,
        chickModel.position.y + 2,
        chickModel.position.z + 3
    );
    camera.lookAt(chickModel.position);

    cameraAnimationStartTime = Date.now();
    isCameraAnimationDone = false;
}

let cameraAnimationStartTime;
let isCameraAnimationDone = false;

function updateCameraDuringAnimation() {
    const elapsedTime = (Date.now() - cameraAnimationStartTime) / 1000;

    if (elapsedTime < 4) {
        const angle = (elapsedTime / 3) * Math.PI;
        const radius = 10;
        camera.position.set(
            chickModel.position.x + Math.cos(angle) * radius,
            chickModel.position.y + 5,
            chickModel.position.z + Math.sin(angle) * radius
        );
        camera.lookAt(chickModel.position);
    } else if (elapsedTime >= 4) {
        console.log("vngpd", elapsedTime);
        isCameraAnimationDone = true;
    }
}

function loadCoinModel(x, z) {
    loader.load("/coin/scene.gltf", function (gltf) {
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

        let coinModel = gltf.scene;
        coinModel.scale.set(0.1, 0.1, 0.1);
        coinModel.position.set(x, 2, z); // 위치 설정
        scene.add(coinModel);

        coinModel.userData = { isCoin: true }; // 충돌 체크를 위한 플래그 설정
    });
}

function loadShoeModel(x, z) {
    loader.load("/hermes_shoe/scene.gltf", function (gltf) {
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

        let shoeModel = gltf.scene;
        shoeModel.scale.set(0.5, 0.5, 0.5);
        shoeModel.position.set(x, 2, z); // 위치 설정
        scene.add(shoeModel);

        shoeModel.userData = { isShoe: true }; // 충돌 체크를 위한 플래그 설정
    });
}

function initCoinModels() {
    const xPositions = [-4, 0, 4]; // 가능한 x 위치
    const generatedCoins = {}; // 각 z 좌표에 대해 생성된 x 위치를 추적하는 객체

    for (let zBase = 0; zBase < 7500; zBase += 1000) {
        let coinCount = 0;

        while (coinCount < 25) {
            const z = zBase + Math.random() * 1000;

            if (!generatedCoins[z]) {
                generatedCoins[z] = [];
            }

            let x;
            do {
                x = xPositions[Math.floor(Math.random() * xPositions.length)];
            } while (generatedCoins[z].includes(x));

            loadCoinModel(x, z);
            generatedCoins[z].push(x);
            coinCount++;

            for (let i = 1; i < 3 + Math.floor(Math.random() * 3); i++) {
                if (coinCount >= 300) break;
                const newZ = z + i * 3;
                loadCoinModel(x, newZ);
                generatedCoins[newZ] = [x];
                coinCount++;
            }
        }
    }
}

function initShoeModels() {
    const xPositions = [-4, 0, 4];
    const generatedShoes = {};

    for (let zBase = 10; zBase < 7500; zBase += 1400) {
        let shoeCount = 0;

        while (shoeCount < 1) {
            const z = zBase + Math.random() * 2000;

            if (!generatedShoes[z]) {
                generatedShoes[z] = [];
            }

            let x;
            do {
                x = xPositions[Math.floor(Math.random() * xPositions.length)];
            } while (generatedShoes[z].includes(x));

            loadShoeModel(x, z);
            generatedShoes[z].push(x);
            shoeCount++;
        }
    }
}

function initRenderer(canvas) {
    const renderer = new THREE.WebGLRenderer({
        canvas: canvas,
        antialias: true,
    });
    renderer.outputEncoding = THREE.sRGBEncoding;
    renderer.localClippingEnabled = true;
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
    return camera;
}

function initControls(camera, renderer) {
    const controls = new OrbitControls(camera, renderer.domElement);
    controls.enableDamping = true;
    controls.dampingFactor = 0.25;
    controls.screenSpacePanning = false;
    controls.maxPolarAngle = Math.PI / 2;
    controls.enableZoom = false;
    return controls;
}

function setupKeyListeners() {
    let keysPressed = {};

    window.addEventListener("keydown", (event) => {
        keysPressed[event.key] = true;

        if (event.key === "ArrowLeft" || event.key === "a") {
            moveSide.value = 4;
        }
        if (event.key === "ArrowRight" || event.key === "d") {
            moveSide.value = -4;
        }
    });

    window.addEventListener("keyup", (event) => {
        keysPressed[event.key] = false;

        if (event.key === "ArrowLeft" || event.key === "a") {
            moveSide.value = 0;
        }
        if (event.key === "ArrowRight" || event.key === "d") {
            moveSide.value = 0;
        }
    });

    return keysPressed;
}

function updateCameraPosition() {
    if (chickModel) {
        const chickPosition = chickModel.position.clone();

        const cameraOffset = new THREE.Vector3(
            0,
            cameraSettings.height,
            -cameraSettings.distance
        );

        camera.position.set(
            0,
            chickPosition.y + cameraOffset.y + 3,
            chickPosition.z + cameraOffset.z
        );

        camera.lookAt(0, chickPosition.y + 3, chickPosition.z);

        gameStore.updateZCoordinate(chickPosition.z);
    }
}

function loadAndPlayBackgroundMusic() {
    soundbg = new THREE.Audio(listener);

    audioLoader.load("/children-running.mp3", function (buffer) {
        soundbg.setBuffer(buffer);
        soundbg.setLoop(true);
        soundbg.setVolume(0.5);
        soundbg.play();
    });
}

function loadCoinSound() {
    coinSound = new THREE.Audio(listener);

    audioLoader.load("/coin-recieved.mp3", function (buffer) {
        coinSound.setBuffer(buffer);
        coinSound.setVolume(0.05);
    });
}

function loadShoeSound() {
    shoeSound = new THREE.Audio(listener);

    audioLoader.load("/item.mp3", function (buffer) {
        shoeSound.setBuffer(buffer);
        shoeSound.setVolume(0.05);
    });
}

function checkForCoinCollisions() {
    if (!chickModel) return;

    const chickBB = new THREE.Box3().setFromObject(chickModel);

    scene.children.forEach((child) => {
        if (child.userData.isCoin) {
            const coinBB = new THREE.Box3().setFromObject(child);

            if (chickBB.intersectsBox(coinBB)) {
                coinScore.value += 0.01;
                coinTotalScore.value += 0.01;
                child.visible = false;
                if (coinSound.isPlaying) {
                    coinSound.stop();
                }
                coinSound.play();
            }
        }
    });
}

let speedTimeout = null;

function checkForShoeCollisions() {
    if (!chickModel) return;

    const chickBB = new THREE.Box3().setFromObject(chickModel);

    scene.children.forEach((child) => {
        if (child.userData.isShoe) {
            const shoeBB = new THREE.Box3().setFromObject(child);

            if (chickBB.intersectsBox(shoeBB)) {
                // scene.remove(child);
                child.visible = false;
                if (speedTimeout) {
                    clearTimeout(speedTimeout);
                }

                autoForwardSpeed.value = 1.0;
                console.log("속도 증가:", autoForwardSpeed.value);

                if (shoeSound.isPlaying) {
                    shoeSound.stop();
                }
                shoeSound.play();

                speedTimeout = setTimeout(() => {
                    autoForwardSpeed.value = 0.5;
                    console.log("속도 복구:", autoForwardSpeed.value);
                    speedTimeout = null;
                }, 3000);
            }
        }
    });
}

function onWindowResize() {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(window.innerWidth, window.innerHeight);
}

const clock = new THREE.Clock();

export {
    autoForwardSpeed,
    camera,
    cameraSettings,
    chickModel,
    coinScore,
    coinTotalScore,
    controls,
    initDraw,
    mixer,
    moveSide,
    renderer,
    scene,
    setupKeyListeners,
    soundbg,
    updateCameraPosition
};

