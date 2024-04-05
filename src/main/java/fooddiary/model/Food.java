package fooddiary.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Food {
    private final String name;
    private final float kcal;
    private final float protein;
    private final float fat;
    private final float carbs;
}
