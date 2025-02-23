import router from "@/router";
import { Mutex } from "async-mutex";
import axios from "axios";
import { httpStatus } from "@/configuration/http-status";

const { VITE_SERVER_URL } = import.meta.env;

export const instance = axios.create({
    baseURL: VITE_SERVER_URL,
});

// Request 발생 시 적용할 기본 속성 설정.
instance.defaults.headers.post["Content-Type"] = "application/json";
instance.defaults.headers.put["Content-Type"] = "application/json";

instance.interceptors.request.use(
    (config) => {
        // 세션 스토리지에서 'accessToken'을 가져옴
        const token = sessionStorage.getItem("accessToken");

        // 토큰이 존재하면, 요청 헤더에 인증 토큰을 추가
        if (token) {
            config.headers["Authorization"] = token;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

let tokenRefreshingMutex = new Mutex();
instance.interceptors.response.use(
    (response) => {
        return response;
    },

    async (error) => {
        const { config, response } = error;

        if (response.config.url === "/login") {
            Swal.fire({
                title: "로그인 실패",
                icon: "error",
            });
            return;
        }

        // 토큰 재발급 진행
        if (response.status === httpStatus.UNAUTHORIZED) {
            // /auth/refresh로 요청을 진행해야 하므로, 기존의 요청을 저장.
            const prevRequest = config;

            // 다른 요청이 토큰 갱신을 시도하고 있으면, 완료될 때까지 기다림
            if (tokenRefreshingMutex.isLocked()) {
                await tokenRefreshingMutex.waitForUnlock();
            }

            // 토큰이 없는 것을 가장 먼저 확인한 요청이 토큰 발급을 시도함
            // Critical Section below =========================================================
            try {
                tokenRefreshingMutex.acquire();

                const refreshResponse = await axios({
                    url: `${VITE_SERVER_URL}/reissue`,
                    method: "post",
                    data: {},
                    withCredentials: true,
                })
                    .then((response) => {
                        console.log(response);
                        return Promise.resolve(response); // 결과를 리턴해서 refreshResponse에 넣어줌
                    })
                    .catch((error) => {
                        console.log(error.response);
                        if (error.response.status === httpStatus.BADREQUEST) {
                            // 리프레시 토큰 갱신이 잘못된 경우
                            // 기존 엑세스 토큰 삭제 후 로그인페이지로 리다이렉트
                            sessionStorage.removeItem("accessToken");
                            delete instance.defaults.headers.common[
                                "Authorization"
                            ];
                            router.replace({ name: "login" });
                        }
                    });

                // 모든 요청에 access token이 포함되어 가도록 보장
                const newAccessToken = refreshResponse.headers.authorization;

                // localStorage에 access token 추가
                sessionStorage.setItem("accessToken", newAccessToken);

                instance.defaults.headers.common["Authorization"] =
                    newAccessToken;

                prevRequest.headers.authorization = newAccessToken;
            } catch (error) {
                console.log(error);
                // 토큰 갱신에 실패하면 Circuit break
                // router.push({ name: "login" });
                return Promise.reject(error);
            } finally {
                tokenRefreshingMutex.release();
            }
            // Critical Section above ========================================================
            // Critical section 내부의 작업을 통해 모든 요청에 access token이 가는 것이 보장됨
            return instance(prevRequest);
        } else if (response.status === httpStatus.FORBIDDEN) {
            router.replace({ name: "login" });
        }

        return Promise.reject(error);
    }
);
