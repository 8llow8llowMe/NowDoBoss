package com.ssafy.backend.domain.member.dto;

public record MemberUpdateRequest(
    String nickname,
    String profileImage
) {

}
