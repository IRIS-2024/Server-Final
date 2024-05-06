package app.iris.missingyou.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "for [PATCH] /member/push request")
@NoArgsConstructor
@Getter
@Setter
public class PushInfoDto {
    private String region;
    private String deviceToken;
}