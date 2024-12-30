package com.ssafy.backend.domain.member.service;

import com.ssafy.backend.domain.member.dto.MemberLoginResponse;
import com.ssafy.backend.domain.member.entity.Member;
import com.ssafy.backend.domain.member.exception.MemberErrorCode;
import com.ssafy.backend.domain.member.exception.MemberException;
import com.ssafy.backend.domain.member.repository.MemberRepository;
import com.ssafy.backend.global.component.jwt.service.JwtTokenService;
import com.ssafy.backend.global.component.oauth.OAuthCodeUrlProvider;
import com.ssafy.backend.global.component.oauth.OAuthMemberClient;
import com.ssafy.backend.global.component.oauth.vendor.enums.OAuthDomain;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final OAuthCodeUrlProvider oAuthCodeUrlProvider;
    private final OAuthMemberClient oAuthMemberClient;
    private final MemberRepository memberRepository;
    private final JwtTokenService jwtTokenService;

    @Override
    public String provideAuthCodeRequestUrlOAuth(OAuthDomain oAuthDomain) {
        return oAuthCodeUrlProvider.provide(oAuthDomain);
    }

    @Override
    public MemberLoginResponse loginOAuth(OAuthDomain oAuthDomain, String authCode) {
        Member oauthMember = oAuthMemberClient.fetch(oAuthDomain, authCode);
        Optional<Member> existingMemberOpt = memberRepository.findByEmail(oauthMember.getEmail());

        if (existingMemberOpt.isPresent()) {
            Member existingMember = existingMemberOpt.get();
            if (!existingMember.getOAuthDomain().equals(oAuthDomain)) {
                throw new MemberException(MemberErrorCode.EXIST_MEMBER_EMAIL);
            }
            return jwtTokenService.issueAndSaveJwtToken(existingMember);
        }
        
        Member member = memberRepository.save(oauthMember);
        return jwtTokenService.issueAndSaveJwtToken(member);
    }
}
