package fooddiary.usda.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FoodNutrient {
    private String nutrientName;
    private float value;
}
