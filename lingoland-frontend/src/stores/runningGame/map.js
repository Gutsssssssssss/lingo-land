import * as THREE from "three";
import { renderer, scene } from "./init";
import { useThreeJsStore } from "./threeStore";

const threeJsStore = useThreeJsStore();
const { loader } = threeJsStore;

function loadMapSection(zPosition) {
    const clipPlanes = [
        new THREE.Plane(new THREE.Vector3(0, 0, -1), zPosition + 1050),
    ];
    loader.load("/pirate/scene.gltf", function (gltf) {
        gltf.scene.traverse((child) => {
            if (child.isMesh) {
                child.material.needsUpdate = true;
                child.material.clippingPlanes = clipPlanes;
                if (child.material.map) {
                    child.material.map.anisotropy =
                        renderer.capabilities.getMaxAnisotropy();
                    child.material.map.needsUpdate = true;
                }
            }
        });

        gltf.scene.scale.set(0.015, 0.015, 0.015); // 크기 1.5배로 확장
        gltf.scene.position.set(0, 0, zPosition);
        scene.add(gltf.scene);
    });
}

export { loadMapSection };
