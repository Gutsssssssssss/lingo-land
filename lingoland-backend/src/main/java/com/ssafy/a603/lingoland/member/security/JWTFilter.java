package com.ssafy.a603.lingoland.member.security;

import com.ssafy.a603.lingoland.member.entity.Member;
import com.ssafy.a603.lingoland.member.service.MemberService;
import com.ssafy.a603.lingoland.member.service.MemberServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailsService memberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if(authorization == null || !authorization.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authorization.split(" ")[1];

        if(accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            PrintWriter writer = response.getWriter();
            writer.print("access token expired");
            // TODO 프론트랑 어떤 코드 줄지 상의
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategoryFromToken(accessToken);

        if(!category.equals("access")) {

            PrintWriter writer = response.getWriter();
            writer.print("invalid access expired");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String loginId = jwtUtil.getLoginIdFromToken(accessToken);
        Integer memberId = jwtUtil.getMemberIdFromToken(accessToken);

        UserDetails userDetails = memberService.loadUserByUsername(loginId);

        if(userDetails == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Member member = Member.builder()
                .id(memberId)
                .loginId(loginId)
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
