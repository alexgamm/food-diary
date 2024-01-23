package fooddiary.usda.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Food {
    private List<FoodNutrient> foodNutrients;

    public Optional<Float> getNutrientValue(String name) {
        return foodNutrients.stream()
                .filter(foodNutrient -> foodNutrient.getNutrientName().equals(name))
                .map(FoodNutrient::getValue)
                .findFirst();

    }
}
