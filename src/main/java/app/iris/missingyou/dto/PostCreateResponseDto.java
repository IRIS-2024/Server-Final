package app.iris.missingyou.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PostCreateResponseDto {
    String genImgUrl;
    long pid;
}
