package app.iris.missingyou.controller;

import app.iris.missingyou.dto.TokenDto;
import app.iris.missingyou.entity.Member;
import app.iris.missingyou.entity.Platform;
import app.iris.missingyou.security.GoogleUser;
import app.iris.missingyou.security.OAuth2Service;
import app.iris.missingyou.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final MemberService memberService;
    private final OAuth2Service oAuth2Service;

    @Operation(summary = "Google 소셜로그인 콜백")
    @GetMapping(value = "/auth/google/callback")
    public ResponseEntity<TokenDto> callback(@RequestParam(name = "code") String code) {
        GoogleUser user = oAuth2Service.getUserInfo(code);

        Member member = memberService.findOrJoin(user, Platform.GOOGLE);
        TokenDto dto = memberService.login(member);

        return ResponseEntity.ok().body(dto);
    }

    @Operation(summary = "refresh token을 이용한 토큰 재발급")
    @GetMapping(value = "/auth/refresh")
    public ResponseEntity<TokenDto> reIssueToken(@RequestHeader(required = true, name = "refreshToken") String refreshToken) {
        TokenDto dto = memberService.reIssueToken(refreshToken);
        return ResponseEntity.ok().body(dto);
    }
}
