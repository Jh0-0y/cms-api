package com.malgn.jwt;

import com.malgn.domain.member.entity.Member;
import com.malgn.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // Spring Security DaoAuthenticationProvider가 로그인 시 호출
    // authenticationManager.authenticate()의 username 파라미터(=email)로 DB 조회
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 이메일: " + email));
        return new CustomUserDetails(member);
    }

    // JWT 필터에서 userId로 최신 유저 정보 조회 시 호출
    // DB를 다시 조회하기 때문에 계정 정지 등 상태 변경이 즉시 반영됨
    @Transactional(readOnly = true)
    public CustomUserDetails loadUserById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원: " + id));
        return new CustomUserDetails(member);
    }

}
