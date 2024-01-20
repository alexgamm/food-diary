package fooddiary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FoodDto {
    private final String name;
    private final float energy;
    private final float totalFat;
    private final float protein;
    private final float carbohydrate;
}
