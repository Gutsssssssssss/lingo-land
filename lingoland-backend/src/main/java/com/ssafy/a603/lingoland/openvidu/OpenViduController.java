package com.ssafy.a603.lingoland.openvidu;

import com.ssafy.a603.lingoland.member.security.CurrentUser;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;
import com.ssafy.a603.lingoland.openvidu.dto.CustomTokenDto;
import com.ssafy.a603.lingoland.openvidu.dto.ConnectionDto;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OpenViduController {

    private final OpenViduService openViduService;


    @PostMapping("/sessions")
    public ResponseEntity<?> createSession(@CurrentUser CustomUserDetails customUserDetails) throws OpenViduJavaClientException, OpenViduHttpException {
        String sessionId = openViduService.createSession();
        String token = openViduService.generateToken(sessionId, customUserDetails, 1);

        CustomTokenDto customTokenDto = CustomTokenDto.builder().sessionId(sessionId).token(token).build();
        return ResponseEntity.status(HttpStatus.OK).body(customTokenDto);
    }

    @PostMapping("/sessions/{sessionId}/connections")
    public ResponseEntity<?> generateToken(@PathVariable(value = "sessionId") String sessionId,
                                           @CurrentUser CustomUserDetails customUserDetails) throws OpenViduJavaClientException, OpenViduHttpException {
        String token = openViduService.generateToken(sessionId, customUserDetails, 2);

        CustomTokenDto customTokenDto = CustomTokenDto.builder().sessionId(sessionId).token(token).build();
        return ResponseEntity.status(HttpStatus.OK).body(customTokenDto);
    }
}
