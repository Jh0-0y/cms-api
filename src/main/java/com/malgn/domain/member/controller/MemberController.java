package com.malgn.domain.member.controller;

import com.malgn.configure.security.principal.CustomUserDetails;
import com.malgn.domain.member.dto.MemberRequest;
import com.malgn.domain.member.dto.MemberResponse;
import com.malgn.domain.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Members", description = "회원 API (내 정보, 닉네임 변경)")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "내 정보 조회", description = "로그인한 회원의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MemberResponse.Me.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content)
    })
    @SecurityRequirement(name = "Cookie Authentication")
    @GetMapping("/me")
    public MemberResponse.Me getMe(@AuthenticationPrincipal CustomUserDetails principal) {
        return memberService.getMe(principal.getMember());
    }

    @Operation(summary = "닉네임 변경", description = """
            로그인한 회원의 닉네임을 변경합니다.

            닉네임은 콘텐츠의 작성자/수정자 표시에 사용됩니다.\s
            `created_by` / `last_modified_by`를 문자열이 아닌 FK로 저장하기 때문에,\s
            닉네임 변경 시 별도의 게시글 UPDATE 없이 모든 콘텐츠에 즉시 반영됩니다.\
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MemberResponse.Me.class))),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "닉네임 중복",
                    content = @Content)
    })
    @SecurityRequirement(name = "Cookie Authentication")
    @PatchMapping("/me/nickname")
    public MemberResponse.Me updateNickname(
            @Validated @RequestBody MemberRequest.UpdateNickname request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return memberService.updateNickname(principal.getMember().getId(), request);
    }

}
