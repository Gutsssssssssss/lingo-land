package com.ssafy.a603.lingoland.openvidu;

import com.ssafy.a603.lingoland.member.security.CustomUserDetails;
import com.ssafy.a603.lingoland.openvidu.dto.ConnectionDto;
import io.openvidu.java.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenViduService {

    private OpenVidu openVidu;
    private String secret;

    public OpenViduService(@Value("${openvidu.url}") String openViduUrl, @Value("${openvidu.secret}") String secret) {
        this.openVidu = new OpenVidu(openViduUrl, secret);
        this.secret = secret;
    }

    /*
     * OpenVidu 세션을 생성하고 세션의 ID를 반환
     * */
    public String createSession() throws OpenViduJavaClientException, OpenViduHttpException {
        SessionProperties properties = new SessionProperties.Builder().build();
        Session session = openVidu.createSession(properties);
        return session.getSessionId();
    }

    /*
     * 주어진 세션 ID에 대해 연결 토큰을 생성하고 반환한다.
     * */
    public String generateToken(String sessionId, CustomUserDetails customUserDetails, int role) throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openVidu.getActiveSession(sessionId);
        String loginId = customUserDetails.getUsername();

        ConnectionProperties properties = new ConnectionProperties.Builder().data(loginId).role(role == 1 ? OpenViduRole.MODERATOR : OpenViduRole.PUBLISHER).build();
        return session.createConnection(properties).getToken();
    }
}
