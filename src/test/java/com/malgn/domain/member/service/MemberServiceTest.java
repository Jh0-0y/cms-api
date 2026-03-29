package com.malgn.domain.member.service;

import com.malgn.common.exception.CustomException;
import com.malgn.domain.member.dto.MemberRequest;
import com.malgn.domain.member.dto.MemberResponse;
import com.malgn.domain.member.entity.Member;
import com.malgn.domain.member.entity.Role;
import com.malgn.domain.member.repository.MemberRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("user@malgn.com")
                .username("사용자")
                .nickname("user")
                .password("encoded")
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);
    }

    @Nested
    @DisplayName("내 정보 조회")
    class GetMe {

        @Test
        @DisplayName("성공")
        void success() {
            MemberResponse.Me result = memberService.getMe(member);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo("user@malgn.com");
            assertThat(result.getNickname()).isEqualTo("user");
            assertThat(result.getRole()).isEqualTo(Role.USER);
        }

    }

    @Nested
    @DisplayName("닉네임 변경")
    class UpdateNickname {

        private MemberRequest.UpdateNickname request;

        @BeforeEach
        void setUp() {
            request = new MemberRequest.UpdateNickname();
            ReflectionTestUtils.setField(request, "nickname", "newNick");
        }

        @Test
        @DisplayName("성공")
        void success() {
            given(memberRepository.existsByNickname("newNick")).willReturn(false);
            given(memberRepository.findById(1L)).willReturn(java.util.Optional.of(member));

            MemberResponse.Me result = memberService.updateNickname(1L, request);

            assertThat(result.getNickname()).isEqualTo("newNick");
        }

        @Test
        @DisplayName("실패 - 닉네임 중복")
        void duplicateNickname() {
            given(memberRepository.existsByNickname("newNick")).willReturn(true);

            assertThatThrownBy(() -> memberService.updateNickname(1L, request))
                    .isInstanceOf(CustomException.class)
                    .satisfies(e -> assertThat(((CustomException) e).getStatus()).isEqualTo(HttpStatus.CONFLICT));
        }

    }

}
