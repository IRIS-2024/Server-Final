package app.iris.missingyou.controller;

import app.iris.missingyou.dto.MemberInfoDto;
import app.iris.missingyou.dto.PushInfoDto;
import app.iris.missingyou.exception.ErrorResponse;
import app.iris.missingyou.security.CustomUserDetails;
import app.iris.missingyou.service.FCMService;
import app.iris.missingyou.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "푸시 알림 정보 수정", security = { @SecurityRequirement(name = "bearerAuth")})
    @PatchMapping(value = "/members/push", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postPushInfo(
            @RequestBody PushInfoDto dto
            ) {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        pushService.setPushInfo(principal, dto);

        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(responseCode = "403", description = "인증 헤더 누락"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)) })})
    @Operation(summary = "관심 지역 정보 조회", security = { @SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = "/members/push/region")
    public ResponseEntity<String> getPushRegion() {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String regionName = pushService.getRegionInfo(principal);

        return ResponseEntity.ok().body(regionName);
    }
}