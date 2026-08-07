package com.sparta.logistics.notification.infrastructure.security;

import com.sparta.logistics.notification.common.security.AuthUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class UserAuthentication extends AbstractAuthenticationToken {

    private final AuthUser principal;

    public UserAuthentication(AuthUser principal) {
        // 권한(Role) 목록을 GrantedAuthority 형태로 스프링 시큐리티에 등록
        super(List.of(new SimpleGrantedAuthority(principal.role())));
        this.principal = principal;
        setAuthenticated(true); // 인증 완료 상태로 설정
    }

    @Override
    public Object getCredentials() {
        return null; // 헤더 기반 인증이므로 비밀번호/토큰 같은 자격 증명은 없음
    }

    @Override
    public AuthUser getPrincipal() {
        return this.principal; // Controller의 @AuthenticationPrincipal로 주입될 객체
    }
}