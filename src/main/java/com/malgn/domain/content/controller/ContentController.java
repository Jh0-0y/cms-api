package com.malgn.domain.content.controller;

import com.malgn.domain.content.dto.ContentRequest;
import com.malgn.domain.content.dto.ContentResponse;
import com.malgn.domain.content.service.ContentService;
import com.malgn.configure.security.principal.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Contents", description = "콘텐츠 CRUD API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/contents")
public class ContentController {

    private final ContentService contentService;

    @Operation(summary = "콘텐츠 목록 조회", description = "콘텐츠 목록을 페이징하여 조회합니다. keyword로 제목/내용 검색이 가능합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ContentResponse.Page.class)))
    @GetMapping
    public ContentResponse.Page getContents(
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(description = "검색 키워드 (제목/내용)") @RequestParam(required = false) String keyword) {
        return contentService.getContents(pageable, keyword);
    }

    @Operation(summary = "콘텐츠 생성", description = "새로운 콘텐츠를 생성합니다. 로그인한 사용자만 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContentResponse.Detail.class))),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content)
    })
    @SecurityRequirement(name = "Cookie Authentication")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ContentResponse.Detail createContent(
            @Validated @RequestBody ContentRequest.Create request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return contentService.createContent(request, principal.getMember());
    }

    @Operation(summary = "콘텐츠 상세 조회", description = "콘텐츠 단건을 조회합니다. 조회 시 조회수가 1 증가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContentResponse.Detail.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 콘텐츠",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ContentResponse.Detail getContent(
            @Parameter(description = "콘텐츠 ID") @PathVariable Long id) {
        return contentService.getContent(id);
    }

    @Operation(summary = "콘텐츠 수정", description = "콘텐츠를 수정합니다. 작성자 본인 또는 ADMIN만 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContentResponse.Detail.class))),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 콘텐츠",
                    content = @Content)
    })
    @SecurityRequirement(name = "Cookie Authentication")
    @PutMapping("/{id}")
    public ContentResponse.Detail updateContent(
            @Parameter(description = "콘텐츠 ID") @PathVariable Long id,
            @Validated @RequestBody ContentRequest.Update request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return contentService.updateContent(id, request, principal.getMember());
    }

    @Operation(summary = "콘텐츠 삭제", description = "콘텐츠를 소프트 삭제합니다. 작성자 본인 또는 ADMIN만 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 콘텐츠",
                    content = @Content)
    })
    @SecurityRequirement(name = "Cookie Authentication")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteContent(
            @Parameter(description = "콘텐츠 ID") @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        contentService.deleteContent(id, principal.getMember());
    }

    @Operation(summary = "삭제된 콘텐츠 복구", description = "소프트 삭제된 콘텐츠를 복구합니다. 작성자 본인 또는 ADMIN만 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "복구 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ContentResponse.Detail.class))),
            @ApiResponse(responseCode = "400", description = "삭제되지 않은 콘텐츠",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "복구 권한 없음",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 콘텐츠",
                    content = @Content)
    })
    @SecurityRequirement(name = "Cookie Authentication")
    @PatchMapping("/{id}/restore")
    public ContentResponse.Detail restoreContent(
            @Parameter(description = "콘텐츠 ID") @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return contentService.restoreContent(id, principal.getMember());
    }

}
