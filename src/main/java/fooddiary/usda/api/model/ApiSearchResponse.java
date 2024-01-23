package fooddiary.usda.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ApiSearchResponse {
    private List<Food> foods;

    public List<Food> getFoodsWithEnergy() {
        if (foods == null) {
            return Collections.emptyList();
        }
        return foods.stream()
                .filter(food -> food.getNutrientValue("Energy").isPresent())
                .collect(Collectors.toList());
    }

    public float getAverageValueOfNutrient(String name) {
        List<Float> nutrientValues =  getFoodsWithEnergy().stream().map(food -> food.getNutrientValue(name).orElse(0f)).toList();
        if (nutrientValues.isEmpty()) {
            return 0;
        }
        double sum = nutrientValues.stream().mapToDouble(Float::floatValue).sum();
        return (float) (sum / nutrientValues.size());
    }
}