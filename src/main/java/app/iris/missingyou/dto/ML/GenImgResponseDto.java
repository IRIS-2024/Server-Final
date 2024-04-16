package app.iris.missingyou.dto.ML;

import lombok.Data;

@Data
public class GenImgResponseDto {
    String signed_url;
    String prompt;
    String img_file_path;
}
