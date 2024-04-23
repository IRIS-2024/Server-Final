package app.iris.missingyou.controller;

import app.iris.missingyou.dto.MemberInfoDto;
import app.iris.missingyou.security.CustomUserDetails;
import app.iris.missingyou.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "유저 정보 요청", security = { @SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = "/member")
    public ResponseEntity<MemberInfoDto> getUserInfo() {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        MemberInfoDto dto = memberService.get(principal);

        return ResponseEntity.ok().body(dto);
    }
}
