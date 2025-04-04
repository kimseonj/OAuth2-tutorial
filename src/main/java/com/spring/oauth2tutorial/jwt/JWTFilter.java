package com.spring.oauth2tutorial.jwt;

import com.spring.oauth2tutorial.dto.CustomOAuth2User;
import com.spring.oauth2tutorial.dto.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Authorization")) {
                token = cookie.getValue();
            }
        }

        // Authorization 검증
        if (token == null) {
            System.out.println("token is null");
            filterChain.doFilter(request, response);
            return;
        }

        // 토근 만료시간 검증
        if (jwtUtil.getExpired(token)) {
            System.out.println("token is expired");

            filterChain.doFilter(request, response);
            return;
        }

        // 토큰에서 값 획득 및 회원생성
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setRole(role);

        // UserDetails에 회원 정보 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDto);
        // 스프링 시큐리티 인증 토큰 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
