package com.malgn.jwt;

import com.malgn.domain.member.entity.Member;

import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long memberId;
    private final String email;
    private final String nickname;
    // DaoAuthenticationProvider가 비밀번호 검증에 사용하므로 실제 해시값 보유
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    // Member 엔티티로부터 생성 (로그인 시 loadUserByUsername, 필터 시 loadUserById에서 사용)
    public CustomUserDetails(Member member) {
        this.memberId = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.password = member.getPassword();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole()));
    }

    @Override
    public String getUsername() { return email; }

    @Override
    public String getPassword() { return password; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    // 계정 정지 등 추가 검증이 필요할 경우 Member 엔티티에 필드를 추가하고 여기서 반환
    @Override
    public boolean isEnabled() { return true; }

}
