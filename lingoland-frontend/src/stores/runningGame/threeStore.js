import { defineStore } from "pinia";
import { GLTFLoader } from "three/examples/jsm/loaders/GLTFLoader";
import * as THREE from "three";

export const useThreeJsStore = defineStore("threeJS", () => {
    /**
     * State
     */

    const loader = new GLTFLoader();
    const listener = new THREE.AudioListener();
    const audioLoader = new THREE.AudioLoader();

    /**
     * actions
     */
    function setAudio(camera) {
        camera.add(listener);
    }

    return {
        loader,
        audioLoader,
        listener,
        setAudio,
    };
});
