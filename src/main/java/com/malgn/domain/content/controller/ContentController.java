package com.malgn.domain.content.controller;

import com.malgn.domain.content.dto.ContentRequest;
import com.malgn.domain.content.dto.ContentResponse;
import com.malgn.domain.content.service.ContentService;
import com.malgn.jwt.CustomUserDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/contents")
public class ContentController {

    private final ContentService contentService;

    @GetMapping
    public ContentResponse.Page getContents(
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String keyword) {
        return contentService.getContents(pageable, keyword);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ContentResponse.Detail createContent(
            @Validated @RequestBody ContentRequest.Create request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return contentService.createContent(request, principal);
    }

    @GetMapping("/{id}")
    public ContentResponse.Detail getContent(@PathVariable Long id) {
        return contentService.getContent(id);
    }

    @PutMapping("/{id}")
    public ContentResponse.Detail updateContent(
            @PathVariable Long id,
            @Validated @RequestBody ContentRequest.Update request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return contentService.updateContent(id, request, principal);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteContent(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        contentService.deleteContent(id, principal);
    }

}
