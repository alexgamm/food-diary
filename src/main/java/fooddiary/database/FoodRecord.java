package fooddiary.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@RequiredArgsConstructor
@Getter
public class FoodRecord {
    private final String id;
    private final String foodName;
    private final Date date;
    private final float grams;
    private final float kcal;
    private final float totalFat;
    private final float protein;
    private final float carbohydrate;
}
