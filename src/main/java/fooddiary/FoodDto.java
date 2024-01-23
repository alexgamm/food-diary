package fooddiary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@RequiredArgsConstructor
@Getter
public class FoodDto {
    private final String id;
    private final String name;
    private final Date date;
    private final float grams;
    private final float energy;
    private final float totalFat;
    private final float protein;
    private final float carbohydrate;
}
