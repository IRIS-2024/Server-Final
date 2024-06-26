package app.iris.missingyou.service;

import app.iris.missingyou.dto.MemberInfoDto;
import app.iris.missingyou.dto.TokenDto;
import app.iris.missingyou.entity.Member;
import app.iris.missingyou.entity.Platform;
import app.iris.missingyou.entity.Push;
import app.iris.missingyou.entity.RefreshToken;
import app.iris.missingyou.exception.CustomException;
import app.iris.missingyou.repository.MemberRepository;
import app.iris.missingyou.repository.PushRepository;
import app.iris.missingyou.repository.RefreshTokenRepository;
import app.iris.missingyou.security.CustomUserDetails;
import app.iris.missingyou.security.GoogleUser;
import app.iris.missingyou.security.jwt.TokenProvider;
import app.iris.missingyou.security.jwt.TokenValidator;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PushRepository pushRepository;
    private final TokenProvider tokenProvider;
    private final TokenValidator tokenValidator;

    @Transactional
    public TokenDto login(Member member) {
        String access = tokenProvider.generateAccessToken(member.getId(), member.getName());
        String refresh = tokenProvider.generateRefreshToken(member.getId());

        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(member.getId()).orElse(null);
        if(refreshToken != null)
            refreshToken.setRefreshToken(refresh);
        else
            refreshToken = new RefreshToken(member, refresh);

        refreshTokenRepository.save(refreshToken);

        return new TokenDto(access, refresh);
    }

    @Transactional
    public void logout(CustomUserDetails userDetails) {
        Member member = findById(Long.parseLong(userDetails.getUsername()));
        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(member.getId()).orElse(null);
        Push push = pushRepository.findByMemberId(member.getId()).orElse(null);

        if(refreshToken != null) {
            refreshToken.setRefreshToken(null);
            refreshTokenRepository.save(refreshToken);
        }
        if(push != null) {
            push.setDeviceToken(null);
            pushRepository.save(push);
        }
    }

    @Transactional
    public TokenDto reIssueToken(String refreshToken) {
        tokenValidator.verifyToken(refreshToken);

        Long id = Long.parseLong(tokenValidator.getSubject(refreshToken));
        RefreshToken stored = refreshTokenRepository.findByMemberId(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "토큰 정보를 찾을 수 없습니다."));

        if(!refreshToken.equals(stored.getRefreshToken()))
            throw new CustomException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다. 다시 로그인해주세요.");

        String name = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."))
                .getName();

        String access = tokenProvider.generateAccessToken(id, name);
        String refresh = tokenProvider.generateRefreshToken(id);
        stored.setRefreshToken(refreshToken);
        refreshTokenRepository.save(stored);

        return new TokenDto(access, refresh);
    }

    @Transactional
    public Member findOrJoin(GoogleUser user, Platform platform) {
        Member member = memberRepository.findByEmailAndPlatform(user.getEmail(), platform)
                .orElse(null);
        if(member == null) {
            member = memberRepository.save(user.toMember());
            pushRepository.save(new Push(member));
        }
        return member;
    }

    public Member findById(Long id){
        return memberRepository.findById(id)
                .orElseThrow(()->new CustomException(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."));
    }

    public MemberInfoDto get(CustomUserDetails userDetails) {
        Member member = findById(Long.parseLong(userDetails.getUsername()));

        return new MemberInfoDto(member.getName(), member.getEmail(), member.getProfile());
    }
}
