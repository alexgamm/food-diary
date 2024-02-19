package fooddiary.fatsecret;

import fooddiary.database.FoodRecord;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class FoodSearch {
    private final HttpRequest httpRequest = new HttpRequest();

    @Nullable
    public FoodRecord findFood(String name, float grams, float energyValue, String personId) {
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
                UUID.randomUUID().toString(),
                personId,
                searchedFood.getName(),
                Instant.now(),
                grams,
                energyValue / 100 * grams,
                searchedFood.getFat() / 100 * grams,
                searchedFood.getProtein() / 100 * grams,
                searchedFood.getCarbs() / 100 * grams
        );
    }

    @Nullable
    public FoodRecord findFood(String name, float grams, String personId) {
        List<Food> foods = httpRequest.search(name);
        if (foods.isEmpty()) {
            return null;
        }
        Food searchedFood = foods.get(0);
        return new FoodRecord(
                UUID.randomUUID().toString(),
                personId,
                searchedFood.getName(),
                Instant.now(),
                grams,
                searchedFood.getKcal() / 100 * grams,
                searchedFood.getFat() / 100 * grams,
                searchedFood.getProtein() / 100 * grams,
                searchedFood.getCarbs() / 100 * grams
        );
    }
}
