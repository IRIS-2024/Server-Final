package app.iris.missingyou.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Schema(description = "for [POST] /member/push request")
@NoArgsConstructor
@Getter
@Setter
public class PushInfoDto {

    private String region;
    @NotNull
    private String deviceToken;
}

