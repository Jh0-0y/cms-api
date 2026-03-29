package com.malgn.domain.member.service;

import com.malgn.common.exception.CustomException;
import com.malgn.domain.member.dto.MemberRequest;
import com.malgn.domain.member.dto.MemberResponse;
import com.malgn.domain.member.entity.Member;
import com.malgn.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberResponse.Me getMe(Member member) {
        return MemberResponse.Me.of(member);
    }

    @Transactional
    public MemberResponse.Me updateNickname(Long memberId, MemberRequest.UpdateNickname request) {
        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
        }
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."));
        member.updateNickname(request.getNickname());
        log.info("닉네임 변경 성공: memberId={}, nickname={}", memberId, request.getNickname());
        return MemberResponse.Me.of(member);
    }

}
