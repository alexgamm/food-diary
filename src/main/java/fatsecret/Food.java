package fatsecret;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Food {
    private final String name;
    private final float kcal;
    private final float protein;
    private final float fat;
    private final float carbs;
}
