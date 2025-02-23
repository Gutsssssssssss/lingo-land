package com.ssafy.a603.lingoland.member.security;

import com.ssafy.a603.lingoland.member.service.MemberServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final MemberServiceImpl memberService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String requestURI = request.getRequestURI();
        if(!requestURI.matches("^\\/api/v1/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if(!requestMethod.equals("PUT")) {
            filterChain.doFilter(request, response);
            return;
        }

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if(refresh == null) {
            // 쿠키에 refresh 토큰이 없음
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            // 토큰이 만료됨
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String category = jwtUtil.getCategoryFromToken(refresh);
        if(!category.equals("refresh")) {
            // refresh 토큰이 아님
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String loginId = jwtUtil.getLoginIdFromToken(refresh);
        if(!memberService.checkExistRefreshToken(loginId)) {
            // 토큰이 없음
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        memberService.deleteRefreshToken(loginId);

        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}