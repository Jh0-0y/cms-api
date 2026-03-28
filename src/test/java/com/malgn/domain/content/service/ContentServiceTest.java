package com.malgn.domain.content.service;

import com.malgn.common.exception.CustomException;
import com.malgn.domain.content.dto.ContentRequest;
import com.malgn.domain.content.dto.ContentResponse;
import com.malgn.domain.content.entity.Content;
import com.malgn.domain.content.repository.ContentRepository;
import com.malgn.domain.member.entity.Member;
import com.malgn.domain.member.repository.MemberRepository;
import com.malgn.jwt.CustomUserDetails;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ContentService contentService;

    private Member member;
    private Member otherMember;
    private Content content;
    private CustomUserDetails userPrincipal;
    private CustomUserDetails adminPrincipal;
    private CustomUserDetails otherPrincipal;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("user@malgn.com")
                .username("사용자")
                .nickname("user")
                .password("encoded")
                .role("USER")
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);

        otherMember = Member.builder()
                .email("other@malgn.com")
                .username("다른사용자")
                .nickname("other")
                .password("encoded")
                .role("USER")
                .build();
        ReflectionTestUtils.setField(otherMember, "id", 3L);

        content = Content.builder()
                .title("테스트 제목")
                .description("테스트 내용")
                .createdBy("user")
                .member(member)
                .build();

        userPrincipal = new CustomUserDetails(1L, "user@malgn.com", "user", "USER");
        adminPrincipal = new CustomUserDetails(2L, "admin@malgn.com", "admin", "ADMIN");
        otherPrincipal = new CustomUserDetails(3L, "other@malgn.com", "other", "USER");
    }

    @Nested
    @DisplayName("단건 조회")
    class GetContent {

        @Test
        @DisplayName("성공 - 조회수 증가")
        void success() {
            given(contentRepository.findById(1L)).willReturn(Optional.of(content));

            ContentResponse.Detail result = contentService.getContent(1L);

            assertThat(result.getTitle()).isEqualTo("테스트 제목");
            then(contentRepository).should(times(1)).increaseViewCount(1L);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 콘텐츠")
        void notFound() {
            given(contentRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> contentService.getContent(99L))
                    .isInstanceOf(CustomException.class)
                    .satisfies(e -> assertThat(((CustomException) e).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
        }

        @Test
        @DisplayName("실패 - 삭제된 콘텐츠")
        void deleted() {
            content.delete();
            given(contentRepository.findById(1L)).willReturn(Optional.of(content));

            assertThatThrownBy(() -> contentService.getContent(1L))
                    .isInstanceOf(CustomException.class)
                    .satisfies(e -> assertThat(((CustomException) e).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
        }

    }

    @Nested
    @DisplayName("콘텐츠 생성")
    class CreateContent {

        @Test
        @DisplayName("성공")
        void success() {
            ContentRequest.Create request = new ContentRequest.Create();
            request.setTitle("새 제목");
            request.setDescription("새 내용");

            given(memberRepository.findById(1L)).willReturn(Optional.of(member));

            ContentResponse.Detail result = contentService.createContent(request, userPrincipal);

            assertThat(result.getTitle()).isEqualTo("새 제목");
            assertThat(result.getCreatedBy()).isEqualTo("user");
            then(contentRepository).should(times(1)).save(org.mockito.ArgumentMatchers.any(Content.class));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 회원")
        void memberNotFound() {
            ContentRequest.Create request = new ContentRequest.Create();
            request.setTitle("새 제목");

            given(memberRepository.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> contentService.createContent(request, userPrincipal))
                    .isInstanceOf(CustomException.class)
                    .satisfies(e -> assertThat(((CustomException) e).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
        }

    }

    @Nested
    @DisplayName("콘텐츠 수정")
    class UpdateContent {

        private ContentRequest.Update request;

        @BeforeEach
        void setUp() {
            request = new ContentRequest.Update();
            request.setTitle("수정된 제목");
            request.setDescription("수정된 내용");
        }

        @Test
        @DisplayName("성공 - 본인")
        void successByOwner() {
            given(contentRepository.findById(1L)).willReturn(Optional.of(content));

            ContentResponse.Detail result = contentService.updateContent(1L, request, userPrincipal);

            assertThat(result.getTitle()).isEqualTo("수정된 제목");
            assertThat(result.getLastModifiedBy()).isEqualTo("user");
        }

        @Test
        @DisplayName("성공 - ADMIN")
        void successByAdmin() {
            given(contentRepository.findById(1L)).willReturn(Optional.of(content));

            ContentResponse.Detail result = contentService.updateContent(1L, request, adminPrincipal);

            assertThat(result.getTitle()).isEqualTo("수정된 제목");
            assertThat(result.getLastModifiedBy()).isEqualTo("admin");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 콘텐츠")
        void notFound() {
            given(contentRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> contentService.updateContent(99L, request, userPrincipal))
                    .isInstanceOf(CustomException.class)
                    .satisfies(e -> assertThat(((CustomException) e).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
        }

        @Test
        @DisplayName("실패 - 삭제된 콘텐츠")
        void deleted() {
            content.delete();
            given(contentRepository.findById(1L)).willReturn(Optional.of(content));

            assertThatThrownBy(() -> contentService.updateContent(1L, request, userPrincipal))
                    .isInstanceOf(CustomException.class)
                    .satisfies(e -> assertThat(((CustomException) e).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
        }

        @Test
        @DisplayName("실패 - 권한 없음")
        void forbidden() {
            given(contentRepository.findById(1L)).willReturn(Optional.of(content));

            assertThatThrownBy(() -> contentService.updateContent(1L, request, otherPrincipal))
                    .isInstanceOf(CustomException.class)
                    .satisfies(e -> assertThat(((CustomException) e).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
        }

    }

    @Nested
    @DisplayName("콘텐츠 삭제")
    class DeleteContent {

        @Test
        @DisplayName("성공 - 본인")
        void successByOwner() {
            given(contentRepository.findById(1L)).willReturn(Optional.of(content));

            contentService.deleteContent(1L, userPrincipal);

            assertThat(content.getIsDeleted()).isTrue();
        }

        @Test
        @DisplayName("성공 - ADMIN")
        void successByAdmin() {
            given(contentRepository.findById(1L)).willReturn(Optional.of(content));

            contentService.deleteContent(1L, adminPrincipal);

            assertThat(content.getIsDeleted()).isTrue();
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 콘텐츠")
        void notFound() {
            given(contentRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> contentService.deleteContent(99L, userPrincipal))
                    .isInstanceOf(CustomException.class)
                    .satisfies(e -> assertThat(((CustomException) e).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
        }

        @Test
        @DisplayName("실패 - 이미 삭제된 콘텐츠")
        void alreadyDeleted() {
            content.delete();
            given(contentRepository.findById(1L)).willReturn(Optional.of(content));

            assertThatThrownBy(() -> contentService.deleteContent(1L, userPrincipal))
                    .isInstanceOf(CustomException.class)
                    .satisfies(e -> assertThat(((CustomException) e).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
        }

        @Test
        @DisplayName("실패 - 권한 없음")
        void forbidden() {
            given(contentRepository.findById(1L)).willReturn(Optional.of(content));

            assertThatThrownBy(() -> contentService.deleteContent(1L, otherPrincipal))
                    .isInstanceOf(CustomException.class)
                    .satisfies(e -> assertThat(((CustomException) e).getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
        }

    }

}
