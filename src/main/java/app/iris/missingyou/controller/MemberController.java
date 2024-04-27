package app.iris.missingyou.controller;

import app.iris.missingyou.dto.MemberInfoDto;
import app.iris.missingyou.dto.PushInfoDto;
import app.iris.missingyou.security.CustomUserDetails;
import app.iris.missingyou.service.FCMService;
import app.iris.missingyou.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;
    private final FCMService pushService;

    @Operation(summary = "유저 정보 요청", security = { @SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = "/member")
    public ResponseEntity<MemberInfoDto> getUserInfo() {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        MemberInfoDto dto = memberService.get(principal);

        return ResponseEntity.ok().body(dto);
    }

    @Operation(summary = "푸시 알림 정보 생성/수정", security = { @SecurityRequirement(name = "bearerAuth")})
    @PutMapping(value = "/members/push", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postPushInfo(
            @RequestBody PushInfoDto dto
            ) {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        pushService.setPushInfo(principal, dto);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "푸시 알림 정보 삭제", security = { @SecurityRequirement(name = "bearerAuth")})
    @DeleteMapping(value = "/members/push")
    public ResponseEntity<?> postPushInfo() {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        pushService.delete(principal);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "관심 지역 정보 조회", security = { @SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = "/members/push/region")
    public ResponseEntity<String> getPushRegion() {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String regionName = pushService.getRegionInfo(principal);

        return ResponseEntity.ok().body(regionName);
    }
}
