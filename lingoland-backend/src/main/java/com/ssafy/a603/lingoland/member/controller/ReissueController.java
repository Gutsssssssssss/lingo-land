package com.ssafy.a603.lingoland.member.controller;

import com.ssafy.a603.lingoland.member.repository.MemberRepository;
import com.ssafy.a603.lingoland.member.service.MemberServiceImpl;
import com.ssafy.a603.lingoland.member.entity.Member;
import com.ssafy.a603.lingoland.member.security.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final MemberServiceImpl memberService;
    private final MemberRepository memberRepository;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if(refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        String category = jwtUtil.getCategoryFromToken(refresh);

        if(!category.equals("refresh")) {
            return new ResponseEntity<>("refresh token invalid", HttpStatus.BAD_REQUEST);
        }

        String loginId = jwtUtil.getLoginIdFromToken(refresh);
        Integer memberId = jwtUtil.getMemberIdFromToken(refresh);

        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new NoSuchElementException("존재하지 않은 유저입니다"));
        if(!refresh.equals(member.getRefreshToken())) {
            return new ResponseEntity<>("refresh token invalid", HttpStatus.BAD_REQUEST);
        }

        String role = jwtUtil.getRoleFromToken(refresh);

        String newAccess = jwtUtil.createToken("access", loginId, memberId, role, 600000L);
        String newRefresh = jwtUtil.createToken("refresh", loginId, memberId, role, 86400000L);

        memberService.addRefreshToken(loginId, newRefresh);

        response.setHeader("Authorization", "Bearer " + newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }
}
