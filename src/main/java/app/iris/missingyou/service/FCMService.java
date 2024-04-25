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
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FCMService {
    private final MemberService memberService;
    private final PushRepository pushRepository;

    public void setPushInfo(CustomUserDetails userDetails, PushInfoDto dto) {
        Member member = memberService.findById(Long.parseLong(userDetails.getUsername()));
        Push push = pushRepository.findByMemberId(member.getId()).orElse(new Push(member));

        Region region = Region.fromName(dto.getRegion());
        if(region != push.getRegion() )
            unsubcribeTopic(dto.getDeviceToken(), region);
        subscribeTopic(dto.getDeviceToken(), region);

        push.setDeviceToken(dto.getDeviceToken());
        push.setRegion(region);

        pushRepository.save(push);
    }

    public void delete(CustomUserDetails userDetails) {
        Member member = memberService.findById(Long.parseLong(userDetails.getUsername()));
        Push push = pushRepository.findByMemberId(member.getId()).orElseThrow(
                ()->new CustomException(HttpStatus.NOT_FOUND, "해당 자원을 찾을 수 없습니다.")
        );

        pushRepository.delete(push);
    }

    public void subscribeTopic(String deviceToken, Region region) {
        List<String> registrationTokens = new ArrayList<>();
        registrationTokens.add(deviceToken);
        TopicManagementResponse response;
        try {
           response = FirebaseMessaging.getInstance()
                   .subscribeToTopic(registrationTokens, region.name());
        }  catch (FirebaseMessagingException e) {
            log.error("fail to subscribe: fcm[ " + e.getErrorCode()+"]"+ e.getMessage() );
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "fcm 서버와 통신에 실패했습니다.");
        } catch (Exception e) {
            log.error("fail to unsubscribe: "+e.getMessage());
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "FCM 서버와 통신에 실패했습니다.");
        }
    }

    public void unsubcribeTopic(String deviceToken, Region region) {
        List<String> registrationTokens = new ArrayList<>();
        registrationTokens.add(deviceToken);
        TopicManagementResponse response;
        try {
            response = FirebaseMessaging.getInstance().unsubscribeFromTopic(
                    registrationTokens, region.name());
        } catch (FirebaseMessagingException e) {
            log.error("fail to unsubscribe: fcm[ " + e.getErrorCode()+"]"+ e.getMessage() );
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "fcm 서버와 통신에 실패했습니다.");
        } catch (Exception e) {
            log.error("fail to unsubscribe: "+e.getMessage());
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "FCM 서버와 통신에 실패했습니다.");
        }
    }

    @Async
    public void sendPush(Long pid, String regionName) {
        try {
            Region region = Region.fromName(regionName);

            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle("새로운 실종 정보")
                            .setBody(regionName+"에서 새로운 실종 정보가 등록되었습니다.")
                            .build())
                    .setTopic(region.name())
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            log.error("fail to send message: fcm[ " + e.getErrorCode()+"]"+ e.getMessage() );
        } catch (Exception e) {
            log.error("fail to send message: "+e.getMessage());
        }
    }
}
