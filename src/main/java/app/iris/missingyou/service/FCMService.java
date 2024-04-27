package app.iris.missingyou.service;

import app.iris.missingyou.dto.PushInfoDto;
import app.iris.missingyou.entity.Member;
import app.iris.missingyou.entity.Push;
import app.iris.missingyou.entity.Region;
import app.iris.missingyou.exception.CustomException;
import app.iris.missingyou.repository.PushRepository;
import app.iris.missingyou.security.CustomUserDetails;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FCMService {
    private final MemberService memberService;
    private final PushRepository pushRepository;

    public String getRegionInfo(CustomUserDetails userDetails) {
        Member member = memberService.findById(Long.parseLong(userDetails.getUsername()));
        Push push = pushRepository.findByMemberId(member.getId()).orElseThrow(
                ()->new CustomException(HttpStatus.NOT_FOUND, "해당 자원을 찾을 수 없습니다.")
        );

        return push.getRegion() != null ? push.getRegion().getName() : null;
    }

    public void setPushInfo(CustomUserDetails userDetails, PushInfoDto dto) {
        Member member = memberService.findById(Long.parseLong(userDetails.getUsername()));
        Push push = pushRepository.findByMemberId(member.getId()).orElse(new Push(member));

        if(!dto.getRegion().isEmpty()){
            Region region = Region.fromName(dto.getRegion());
            push.setRegion(region);
        }
        push.setDeviceToken(dto.getDeviceToken());

        pushRepository.save(push);
    }

    public void delete(CustomUserDetails userDetails) {
        Member member = memberService.findById(Long.parseLong(userDetails.getUsername()));
        Push push = pushRepository.findByMemberId(member.getId()).orElseThrow(
                ()->new CustomException(HttpStatus.NOT_FOUND, "해당 자원을 찾을 수 없습니다.")
        );

        pushRepository.delete(push);
    }

    @Async
    public void sendPush(Long pid, String regionName) {
        try {
            Region region = Region.fromName(regionName);
            Page<String> page = pushRepository.findAllDeviceToken(region, PageRequest.of(0, 500));
            while (page.hasNext()) {
                List<String> deviceTokenList = page.getContent();
                requestPush(pid, regionName, deviceTokenList);

                Pageable pageable = page.nextPageable();
                page = pushRepository.findAllDeviceToken(region, pageable);
            }
        } catch (Exception e) {
            log.error("fail to send message: "+e.getMessage());
        }
    }

    @Async
    public void requestPush(Long pid,String regionName ,List<String> tokenList) {
        try {
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle("새로운 실종 정보")
                            .setBody(regionName+"에서 새로운 실종 정보가 등록되었습니다.")
                            .build())
                    .putData("pid", pid.toString())
                    .addAllTokens(tokenList)
                    .build();
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
        } catch (FirebaseMessagingException e) {
            log.error("fail to send message: fcm[ " + e.getErrorCode()+"]"+ e.getMessage() );
        }
    }
}