package com.malgn.configure.security.detail;

import com.malgn.common.exception.CustomException;
import com.malgn.domain.member.entity.Member;
import com.malgn.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public CustomUserDetails loadUserById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.UNAUTHORIZED, "존재하지 않는 회원입니다."));
        return new CustomUserDetails(member);
    }

}
