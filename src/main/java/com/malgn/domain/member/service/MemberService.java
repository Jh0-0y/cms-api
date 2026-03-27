package com.malgn.domain.member.service;

import com.malgn.common.exception.CustomException;
import com.malgn.domain.member.dto.MemberRequest;
import com.malgn.domain.member.entity.Member;
import com.malgn.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(MemberRequest.Signup request) {

        if (memberRepository.existsByEmail(request.getEmail())) {
            log.debug("회원가입 실패 - 이메일 중복: {}", request.getEmail());
            throw new CustomException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }
        if (memberRepository.existsByNickname(request.getNickname())) {
            log.debug("회원가입 실패 - 닉네임 중복: {}", request.getNickname());
            throw new CustomException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        memberRepository.save(member);
        log.info("회원가입 성공: email={}", request.getEmail());
    }

}
