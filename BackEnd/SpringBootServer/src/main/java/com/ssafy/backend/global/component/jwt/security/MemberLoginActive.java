package com.ssafy.backend.global.component.jwt.security;

import com.ssafy.backend.domain.member.entity.enums.MemberRole;

public record MemberLoginActive(
    Long id,
    MemberRole role
) {

}
