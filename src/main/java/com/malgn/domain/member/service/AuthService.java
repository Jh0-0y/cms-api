package com.malgn.domain.member.service;

import com.malgn.common.exception.CustomException;
import com.malgn.domain.member.dto.MemberRequest;
import com.malgn.domain.member.dto.MemberResponse;
import com.malgn.domain.member.entity.Member;
import com.malgn.domain.member.entity.RefreshToken;
import com.malgn.domain.member.repository.MemberRepository;
import com.malgn.domain.member.repository.RefreshTokenRepository;
import com.malgn.configure.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
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

    @Transactional
    public MemberResponse.Token login(MemberRequest.Login request) {

        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.debug("로그인 실패 - 존재하지 않는 이메일: {}", request.getEmail());
                    return new CustomException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
                });

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            log.debug("로그인 실패 - 비밀번호 불일치: email={}", request.getEmail());
            throw new CustomException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        refreshTokenRepository.findByMemberId(member.getId())
                .ifPresentOrElse(
                        savedToken -> savedToken.updateToken(refreshToken),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .token(refreshToken)
                                .member(member)
                                .build())
                );

        log.info("로그인 성공: email={}", member.getEmail());
        return MemberResponse.Token.of(accessToken, refreshToken);
    }

    @Transactional
    public MemberResponse.AccessToken refresh(MemberRequest.Refresh request) {
        log.debug("토큰 재발급 시도");

        Long memberId = jwtTokenProvider.getMemberId(request.getRefreshToken());

        RefreshToken savedToken = refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> {
                    log.debug("토큰 재발급 실패 - DB에 토큰 없음: memberId={}", memberId);
                    return new CustomException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
                });

        if (!savedToken.getToken().equals(request.getRefreshToken())) {
            log.debug("토큰 재발급 실패 - 토큰 불일치: memberId={}", memberId);
            throw new CustomException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        // Access Token에 userId만 포함하므로 member 조회 불필요
        String newAccessToken = jwtTokenProvider.createAccessToken(memberId);

        log.info("토큰 재발급 성공: memberId={}", memberId);
        return MemberResponse.AccessToken.of(newAccessToken);
    }

    @Transactional
    public void logout(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
        log.info("로그아웃 성공: memberId={}", memberId);
    }

}
