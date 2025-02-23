import * as THREE from "three";

// 병아리
function addLights(scene) {
    const light = new THREE.DirectionalLight(0xffffff, 1);
    light.position.set(3, 10, 10);
    scene.add(light);

    const light2 = new THREE.DirectionalLight(0xffffff, 1);
    light2.position.set(0, 10, -10);
    scene.add(light2);

    const ambientLight = new THREE.AmbientLight(0x404040); // 주변 조명
    scene.add(ambientLight);

    const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
    directionalLight.position.set(5, 10, 5).normalize();
    scene.add(directionalLight);

    const directionalLight2 = new THREE.DirectionalLight(0xffffff, 0.5);
    directionalLight2.position.set(-5, 10, -5).normalize();
    scene.add(directionalLight2);
}

export { addLights };
