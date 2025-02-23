import { httpStatus } from "@/configuration/http-status";
import defaultGroupImage from "@/assets/sampleImg.jpg";
import { defineStore } from "pinia";
import swal from "sweetalert2";
import { inject, ref } from "vue";
import { useRouter } from "vue-router";

const { VITE_SERVER_IMAGE_URL } = import.meta.env;

export const useUserStore = defineStore("userStore", () => {
    /**
     * State
     */
    window.Swal = swal;

    const router = useRouter();
    const axios = inject("axios");
    const isAuthenticated = ref(false);

    /**
     * actions
     */
    // 이미지 얻어왔을 때 기본 이미지 넣어주는 함수 & 이미지 경로 넣어주는 함수 구현
    function checkImagePath(profileImage) {
        if (profileImage === "member/default.jpg") {
            return defaultGroupImage;
        } else {
            return VITE_SERVER_IMAGE_URL + profileImage;
        }
    }

    function checkPassword(originPassword, checkPassword) {
        // 비밀번호와 비밀번호 확인이 일치한지 확인
        if (originPassword === checkPassword) {
            return true;
        } else {
            return false;
        }
    }

    // id 중복 체크
    const checkDuplicate = async (loginId) => {
        let return_value;
        await axios
            .get(`/users/check/${loginId}`, { withCredentials: true })
            .then((response) => {
                if (response.status === httpStatus.OK) {
                    return_value = true;
                }
            })
            .catch((error) => {
                console.log(error);
                if (error.response.status === httpStatus.CONFLICT) {
                    return_value = false;
                }
            });

        return return_value;
    };

    // 회원가입
    const signUp = async (userInfo) => {
        await axios
            .post("/users/sign-up", userInfo, { withCredentials: true })
            .then((response) => {
                if (response.status === httpStatus.CREATE) {
                    Swal.fire({
                        title: "회원가입 완료",
                        icon: "success",
                        confirmButtonText: "완료",
                    }).then(() => {
                        router.replace({ name: "login" });
                    });
                } else {
                    Swal.fire({
                        title: "회원가입 실패",
                        icon: "error",
                    });
                }
            })
            .catch((error) => {
                console.log(error);
            });
    };

    // 로그인
    const login = async (userInfo) => {
        await axios
            .post("/login", userInfo, { withCredentials: true })
            .then((response) => {
                if (response.status === httpStatus.OK) {
                    isAuthenticated.value = true;

                    // axios 요청 header에 access token 추가
                    axios.defaults.headers.common["Authorization"] =
                        response.headers.authorization;

                    // localStorage에 access token 추가
                    sessionStorage.setItem(
                        "accessToken",
                        response.headers.authorization
                    );

                    router.replace({ name: "mainPage" });
                }
            })
            .catch((error) => {
                console.log(error);
            });
    };

    // 로그아웃
    const logout = async () => {
        await axios
            .put("/logout", {}, { withCredentials: true })
            .then((response) => {
                isAuthenticated.value = false;
                sessionStorage.removeItem("accessToken");
                delete axios.defaults.headers.common["Authorization"];
                router.replace({ name: "login" });
            })
            .catch((error) => {
                console.log(error);
            });
    };

    // 유저 프로필 조회
    const getProfile = async () => {
        const userProfile = await axios
            .get("/users", { withCredentials: true })
            .then((response) => {
                if (response.status === httpStatus.OK) {
                    response.data.profileImage = checkImagePath(
                        response.data.profileImage
                    );
                    return Promise.resolve(response.data);
                }
            })
            .catch((error) => {
                console.log(error);
            });

        return userProfile;
    };

    // 유저 프로필 조회(loginId 기준)
    const getProfileById = async (loginId) => {
        const userProfile = await axios
            .get(`/users/${loginId}`, { withCredentials: true })
            .then((response) => {
                if (response.status === httpStatus.OK) {
                    response.data.profileImage = checkImagePath(
                        response.data.profileImage
                    );
                    return Promise.resolve(response.data);
                }
            })
            .catch((error) => {
                console.log(error);
            });

        return userProfile;
    };

    // 유저 닉네임 수정
    const modifyNickname = async (newNickname) => {
        await axios
            .put(
                "/users/nickname",
                { nickname: newNickname },
                { withCredentials: true }
            )
            .then((response) => {
                if (response.status === httpStatus.OK) {
                    console.log(response);
                    Swal.fire({
                        title: "닉네임 수정 완료",
                        icon: "success",
                        confirmButtonText: "완료",
                    }).then(() => {
                        router.replace({ name: "myPage" });
                    });
                }
            })
            .catch((error) => {
                console.log(error);
            });
    };

    // 유저 비밀번호 수정
    const modifyPassword = async (passwordDTO) => {
        await axios
            .put("/users/password", passwordDTO, { withCredentials: true })
            .then((response) => {
                if (response.status === httpStatus.OK) {
                    console.log(response);
                    Swal.fire({
                        title: "비밀번호 수정 완료",
                        icon: "success",
                        confirmButtonText: "완료",
                    }).then(() => {
                        router.replace({ name: "myPage" });
                    });
                }
            })
            .catch((error) => {
                console.log(error);
                if (error.response.status === httpStatus.BADREQUEST) {
                    Swal.fire({
                        title: error.response.data,
                        icon: "error",
                    });
                }
            });
    };

    // 유저 이미지 수정
    const modifyProfileImage = async (profileImage) => {
        const formData = new FormData();

        formData.append("profileImage", profileImage);

        await axios
            .put("/users/profile-image", formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
                withCredentials: true,
            })
            .then((response) => {
                if (response.status === httpStatus.OK) {
                    Swal.fire({
                        title: "이미지 수정이 완료되었어요",
                        icon: "success",
                    }).then(() => {
                        router.go(0);
                    });
                }
            })
            .catch((error) => {
                console.log(error);
                if (error.response.status === httpStatus.BADREQUEST) {
                    Swal.fire({
                        title: error.response.data,
                        icon: "error",
                    });
                }
            });
    };

    return {
        isAuthenticated,
        checkPassword,
        checkDuplicate,
        signUp,
        login,
        logout,
        getProfile,
        getProfileById,
        modifyNickname,
        modifyPassword,
        modifyProfileImage,
    };
});
