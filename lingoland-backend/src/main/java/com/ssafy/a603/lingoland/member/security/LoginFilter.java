package com.ssafy.a603.lingoland.member.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.a603.lingoland.member.service.MemberServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final MemberServiceImpl memberService;
    private final ObjectMapper objectMapper;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, MemberServiceImpl memberService, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.memberService = memberService;
        this.objectMapper = objectMapper;
        setFilterProcessesUrl("/api/v1/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if(!request.getMethod().equals("POST")) {
            throw new RuntimeException("Authentication method not supported: " + request.getMethod());
        }

        Map<String, String> loginData = null;
        try {
            loginData = objectMapper.readValue(request.getInputStream(), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String loginId = loginData.get("loginId");
        String password = loginData.get("password");

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginId, password, null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String loginId = customUserDetails.getUsername();
        Integer memberId = customUserDetails.getMemberId();

        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
        Optional<? extends GrantedAuthority> authorityOptional = authorities.stream().findFirst();

        if(!authorityOptional.isPresent()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "권한이 없는 유저입니다.");
            return;
        }

        GrantedAuthority auth = authorityOptional.get();
        String role = auth.getAuthority();

        String access = jwtUtil.createToken("access", loginId, memberId, role, 600000L);
        String refresh = jwtUtil.createToken("refresh", loginId, memberId, role, 86400000L);

        memberService.addRefreshToken(loginId, refresh);

        response.addHeader("Authorization", "Bearer " + access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

}
