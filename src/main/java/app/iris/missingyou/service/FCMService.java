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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class FCMService {
    private final MemberService memberService;
    private final PushRepository pushRepository;

    public String getRegionInfo(CustomUserDetails userDetails) {
        Member member = memberService.findById(Long.parseLong(userDetails.getUsername()));

        Region region = pushRepository.findByMemberId(member.getId()).orElseThrow(
                ()->new CustomException(HttpStatus.NOT_FOUND, "해당 자원을 찾을 수 없습니다.")
        ).getRegion();

        return  region == null ? null : region.getName();
    }

    @Transactional
    public void setPushInfo(CustomUserDetails userDetails, PushInfoDto dto) {
        Member member = memberService.findById(Long.parseLong(userDetails.getUsername()));
        Push push = pushRepository.findByMemberId(member.getId()).orElseThrow(
                ()->new CustomException(HttpStatus.NOT_FOUND, "해당 자원을 찾을 수 없습니다.")
        );

        if (dto.getDeviceToken() == null) {
            push.setRegion(null);
            return;
        }

        if (dto.getRegion() != null) {
            push.setRegion(Region.fromName(dto.getRegion()));
        }
        push.setDeviceToken(dto.getDeviceToken());

        pushRepository.save(push);
    }

    @Async
    public void sendPush(Long pid, String regionName, Long memberId) {
        try {
            Region region = Region.fromName(regionName);
            Page<String> page = pushRepository.findAllDeviceToken(region, memberId, PageRequest.of(0, 500));

            for(int i=0; i<page.getTotalPages(); i++) {
                List<String> deviceTokenList = page.getContent();
                requestPush(pid, regionName, deviceTokenList);
                Pageable pageable = page.nextPageable();
                page = pushRepository.findAllDeviceToken(region, memberId ,pageable);
            }
        } catch (Exception e) {
            log.error("fail to send message: "+e.getMessage());
        }
    }

    @Async
    public void requestPush(Long pid, String regionName ,List<String> tokenList) {
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
            log.info("{}/{} notifications have been sent for posts on pid{}.", response.getSuccessCount(), tokenList.size(), pid);
        } catch (FirebaseMessagingException e) {
            log.error("fail to send message: fcm[ " + e.getErrorCode()+"]"+ e.getMessage() );
        }
    }

    @Async
    public void requestPush(Long pid) {
        try{
            String token = pushRepository.findPostAuthorDeviceToken(pid).get();
            Message message = Message.builder()
                    .putData("pid", pid.toString())
                    .setNotification(Notification.builder()
                            .setTitle("제보 댓글 등록 알림")
                            .setBody("실종 정보글에 새로운 제보 댓글이 등록되었습니다.")
                            .build())
                    .setToken(token)
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("제보 댓글 알림 요청 성공 pid: {}", pid);
        } catch (FirebaseMessagingException e) {
            log.error("fail to send message: fcm[ " + e.getErrorCode()+"]"+ e.getMessage() );
        } catch (NoSuchElementException e) {
            log.warn("fail to send message: token was not found pid: {}", pid);
        } catch (Exception e) {
            log.error("fail to send message: "+e.getMessage());
        }
    }
}