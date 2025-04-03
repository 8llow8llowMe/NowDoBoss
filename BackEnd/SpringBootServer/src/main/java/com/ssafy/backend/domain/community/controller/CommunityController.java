package com.ssafy.backend.domain.community.controller;

import com.ssafy.backend.domain.community.dto.request.CommunityListRequest;
import com.ssafy.backend.domain.community.dto.request.CreateCommentRequest;
import com.ssafy.backend.domain.community.dto.request.CreateCommunityRequest;
import com.ssafy.backend.domain.community.dto.request.UpdateCommentRequest;
import com.ssafy.backend.domain.community.dto.request.UpdateCommunityRequest;
import com.ssafy.backend.domain.community.dto.response.CommentListResponse;
import com.ssafy.backend.domain.community.dto.response.CommunityDetailResponse;
import com.ssafy.backend.domain.community.dto.response.CommunityListResponse;
import com.ssafy.backend.domain.community.dto.response.PopularCommunityListResponse;
import com.ssafy.backend.domain.community.service.CommentService;
import com.ssafy.backend.domain.community.service.CommunityService;
import com.ssafy.backend.global.annotation.PublicEndpoint;
import com.ssafy.backend.global.common.dto.Response;
import com.ssafy.backend.global.component.jwt.security.MemberLoginActive;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "커뮤니티", description = "커뮤니티 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
public class CommunityController {

    private final CommunityService communityService;
    private final CommentService commentService;

    @Operation(
        summary = "게시글 작성",
        description = "커뮤니티 게시글을 작성하는 기능입니다."
    )
    @PostMapping
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> createCommunity(
        @AuthenticationPrincipal MemberLoginActive loginActive,
        @Validated @RequestBody CreateCommunityRequest request) {

        communityService.createCommunity(loginActive.id(), request);
        return ResponseEntity.ok().body(Response.success());
    }

    @Operation(
        summary = "게시글 목록 조회",
        description = "커뮤니티 게시글 목록을 조회하는 기능입니다."
    )
    @PublicEndpoint
    @GetMapping
    public ResponseEntity<Response<List<CommunityListResponse>>> selectCommunityList(
        CommunityListRequest request) {
        return ResponseEntity.ok()
            .body(Response.success(communityService.selectCommunityList(request)));
    }

    @Operation(
        summary = "인기 게시글 조회",
        description = "커뮤니티 인기 게시글을 조회하는 기능입니다."
    )
    @PublicEndpoint
    @GetMapping("/popular")
    public ResponseEntity<Response<List<PopularCommunityListResponse>>> selectPopularCommunityList() {
        return ResponseEntity.ok()
            .body(Response.success(communityService.selectPopularCommunityList()));
    }

    @Operation(
        summary = "게시글 상세 조회",
        description = "커뮤니티 게시글을 상세 조회하는 기능입니다."
    )
    @PublicEndpoint
    @GetMapping("/{communityId}")
    public ResponseEntity<Response<CommunityDetailResponse>> selectCommunity(
        @PathVariable Long communityId) {
        return ResponseEntity.ok()
            .body(Response.success(communityService.selectCommunity(communityId)));
    }

    @Operation(
        summary = "게시글 삭제",
        description = "커뮤니티 게시글을 삭제하는 기능입니다."
    )
    @DeleteMapping("/{communityId}")
    public ResponseEntity<Response<Void>> deleteCommunity(@PathVariable Long communityId) {
        communityService.deleteCommunity(communityId);
        return ResponseEntity.ok().body(Response.success());
    }

    @Operation(
        summary = "게시글 수정",
        description = "커뮤니티 게시글을 수정하는 기능입니다."
    )
    @PatchMapping("/{communityId}")
    public ResponseEntity<Response<Void>> updateCommunity(@PathVariable Long communityId,
        @Validated @RequestBody UpdateCommunityRequest request) {
        communityService.updateCommunity(communityId, request);
        return ResponseEntity.ok().body(Response.success());
    }

    @Operation(
        summary = "댓글 작성",
        description = "커뮤니티 댓글을 작성하는 기능입니다."
    )
    @PostMapping("/{communityId}/comment")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> createComment(
        @AuthenticationPrincipal MemberLoginActive loginActive,
        @PathVariable Long communityId,
        @Validated @RequestBody CreateCommentRequest request) {
        commentService.createComment(loginActive.id(), communityId, request.content());
        return ResponseEntity.ok().body(Response.success());
    }

    @Operation(
        summary = "댓글 목록 조회",
        description = "커뮤니티 댓글 목록을 조회하는 기능입니다."
    )
    @PublicEndpoint
    @GetMapping("/{communityId}/comment")
    public ResponseEntity<Response<List<CommentListResponse>>> selectCommentList(
        @PathVariable Long communityId, Long lastId) {
        return ResponseEntity.ok().body(
            Response.success(commentService.selectCommentList(communityId, lastId)));
    }

    @Operation(
        summary = "댓글 삭제 ",
        description = "커뮤니티 댓글을 삭제하는 기능입니다."
    )
    @DeleteMapping("/{communityId}/comment/{commentId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> deleteComment(@PathVariable Long communityId,
        @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().body(Response.success());
    }

    @Operation(
        summary = "댓글 수정 ",
        description = "커뮤니티 댓글을 수정하는 기능입니다."
    )
    @PatchMapping("/{communityId}/comment/{commentId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> updateComment(@PathVariable Long commentId,
        @Validated @RequestBody UpdateCommentRequest request) {
        commentService.updateComment(commentId, request);
        return ResponseEntity.ok().body(Response.success());
    }
}
