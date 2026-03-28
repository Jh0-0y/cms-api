package com.malgn.configure.security.detail;

import com.malgn.domain.member.entity.Member;

import lombok.Getter;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@NullMarked
public class CustomUserDetails implements UserDetails {

    private final Member member;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Member member) {
        this.member = member;
        this.authorities = List.of(new SimpleGrantedAuthority(member.getRole().toAuthority()));
    }

    @Override
    public String getUsername() { return member.getEmail(); }

    @Override
    public String getPassword() { return member.getPassword(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

}
