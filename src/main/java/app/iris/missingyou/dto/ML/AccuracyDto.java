package app.iris.missingyou.dto.ML;

import lombok.Data;

@Data
public class AccuracyDto {
    double distance;
    double threshhold;
    double confidence_percent;
}
