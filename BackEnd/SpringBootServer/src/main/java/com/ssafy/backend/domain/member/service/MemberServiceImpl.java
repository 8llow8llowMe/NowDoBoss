package com.ssafy.backend.domain.member.service;

import com.ssafy.backend.domain.commercial.repository.CommercialAnalysisRepository;
import com.ssafy.backend.domain.member.dto.MemberInfo;
import com.ssafy.backend.domain.member.dto.MemberLoginRequest;
import com.ssafy.backend.domain.member.dto.MemberLoginResponse;
import com.ssafy.backend.domain.member.dto.MemberPasswordChangeRequest;
import com.ssafy.backend.domain.member.dto.MemberSignupRequest;
import com.ssafy.backend.domain.member.dto.MemberUpdateRequest;
import com.ssafy.backend.domain.member.entity.Member;
import com.ssafy.backend.domain.member.exception.MemberErrorCode;
import com.ssafy.backend.domain.member.exception.MemberException;
import com.ssafy.backend.domain.member.repository.MemberRepository;
import com.ssafy.backend.domain.recommendation.repository.RecommendationRepository;
import com.ssafy.backend.domain.simulation.repository.SimulationRepository;
import com.ssafy.backend.global.component.jwt.repository.RefreshTokenRepository;
import com.ssafy.backend.global.component.jwt.service.JwtTokenService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SimulationRepository simulationRepository;
    private final RecommendationRepository recommendationRepository;
    private final CommercialAnalysisRepository commercialAnalysisRepository;

    @Override
    public void signupMember(MemberSignupRequest signupRequest) {
        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            throw new MemberException(MemberErrorCode.EXIST_MEMBER_EMAIL);
        }

        signupRequest.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        memberRepository.save(signupRequest.toEntity());
    }

    @Override
    public MemberLoginResponse loginMember(MemberLoginRequest loginRequest) {
        Member member = findMemberByEmail(loginRequest.email());

        String realPassword = member.getPassword();

        if (!passwordEncoder.matches(loginRequest.password(), realPassword)) {
            throw new MemberException(MemberErrorCode.NOT_MATCH_PASSWORD);
        }

        return jwtTokenService.issueAndSaveJwtToken(member);
    }

    @Override
    public void logoutMember(String email) {
        Optional<String> token = refreshTokenRepository.find(email);

        if (token.isEmpty()) {
            throw new MemberException(MemberErrorCode.ALREADY_MEMBER_LOGOUT);
        }

        // 리프레쉬 토큰 삭제
        refreshTokenRepository.delete(email);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberInfo getMember(Long memberId) {
        Member member = findMemberById(memberId);

        return new MemberInfo(
            member.getId(),
            member.getEmail(),
            member.getName(),
            member.getNickname(),
            member.getProfileImage(),
            member.getRole(),
            member.getOAuthDomain()
        );
    }

    @Override
    public void deleteMember(Long memberId) {
        // MongoDB에 저장된 데이터 삭제
        simulationRepository.deleteByMemberId(memberId);
        recommendationRepository.deleteByUserId(memberId);
        commercialAnalysisRepository.deleteByMemberId(memberId);

        // JPA에 저장된 데이터 삭제
        memberRepository.deleteById(memberId);
    }

    @Override
    public void updateProfileImageAndNickNameMember(Long memberId,
        MemberUpdateRequest updateRequest) {
        Member member = findMemberById(memberId);

        member.updateProfileImageAndNickname(updateRequest);
    }

    @Override
    public void updatePasswordMember(Long memberId,
        MemberPasswordChangeRequest passwordChangeRequest) {
        Member member = findMemberById(memberId);

        String realPassword = member.getPassword();

        // 현재 비밀번호 제대로 입력했는지 확인
        if (!passwordEncoder.matches(passwordChangeRequest.nowPassword(), realPassword)) {
            throw new MemberException(MemberErrorCode.NOT_MATCH_PASSWORD);
        }

        // 현재 비밀번호와 변경하려는 비밀번호가 같은지 확인 (같은 경우 Exception 발생)
        if (passwordEncoder.matches(passwordChangeRequest.changePassword(), realPassword)) {
            throw new MemberException(MemberErrorCode.CURRENT_CHANGE_MATCH_PASSWORD);
        }

        // 비밀번호 변경과 비밀번호 변경 확인 서로 같은지 확인 (다른 경우 Exception 발생)
        if (!passwordChangeRequest.changePassword()
            .equals(passwordChangeRequest.changePasswordCheck())) {
            throw new MemberException(MemberErrorCode.PASSWORD_CONFIRMATION_MISMATCH);
        }

        member.updatePassword(passwordEncoder.encode(passwordChangeRequest.changePassword()));
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));
    }
}
