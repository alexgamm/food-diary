package fooddiary.fatsecret;

import fooddiary.database.FoodRecord;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class FoodSearch {
    private final HttpRequest httpRequest = new HttpRequest();

    @Nullable
    public FoodRecord findFood(String name, float grams, float energyValue) {
        List<Food> foods = httpRequest.search(name);
        Food searchedFood = foods.stream()
                .min(Comparator.comparing(food -> Math.abs(
                        food.getKcal() - energyValue
                )))
                .orElse(null);

        if (searchedFood == null) {
            return null;
        }
        return new FoodRecord(
                null,
                searchedFood.getName(),
                null,
                grams,
                energyValue / 100 * grams,
                searchedFood.getFat() / 100 * grams,
                searchedFood.getProtein() / 100 * grams,
                searchedFood.getCarbs() / 100 * grams
        );
    }

    @Nullable
    public FoodRecord findFood(String name, float grams) {
        List<Food> foods = httpRequest.search(name);
        if (foods.isEmpty()) {
            return null;
        }
        Food searchedFood = foods.get(0);
        return new FoodRecord(
                null,
                searchedFood.getName(),
                null,
                grams,
                searchedFood.getKcal() / 100 * grams,
                searchedFood.getFat() / 100 * grams,
                searchedFood.getProtein() / 100 * grams,
                searchedFood.getCarbs() / 100 * grams
        );
    }
}
