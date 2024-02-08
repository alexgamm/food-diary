package fooddiary.usda.api;

import fooddiary.database.FoodRecord;
import fooddiary.usda.api.model.ApiSearchResponse;
import fooddiary.usda.api.model.DataType;
import fooddiary.usda.api.model.Food;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ApiHttpRequests {

    private final UsdaApiClient usdaApiClient;

    public FoodRecord findFood(String name, float grams, float energyValue) {
        ApiSearchResponse apiSearchResponse = usdaApiClient.search(name, null);
        List<Food> foods = apiSearchResponse.getFoodsWithEnergy();
        Food searchedFood = foods.stream()
                .min(Comparator.comparing(food -> Math.abs(
                        food.getNutrientValue("Energy").orElse(0f) - energyValue
                )))
                .orElse(null);

        if (searchedFood == null) {
            return new FoodRecord(UUID.randomUUID().toString(), name, new Date(), grams, 0, 0, 0, 0);
        }
        return new FoodRecord(
                UUID.randomUUID().toString(),
                name,
                new Date(),
                grams,
                energyValue / 100 * grams,
                searchedFood
                        .getNutrientValue("Total lipid (fat)")
                        .map(value -> value / 100 * grams)
                        .orElse(0f),
                searchedFood
                        .getNutrientValue("Protein").orElse(0f) / 100 * grams,
                searchedFood.getNutrientValue("Carbohydrate, by difference").orElse(0f) / 100 * grams
        );
    }

    public FoodRecord findBasicFood(String name, float grams) {
        ApiSearchResponse apiSearchResponse = usdaApiClient.search(name, DataType.Foundation);
        return calculateEatenFood(apiSearchResponse, name, grams);
    }

    public FoodRecord findHomeFood(String name, float grams) {
        ApiSearchResponse apiSearchResponse = usdaApiClient.search(name, null);
        return calculateEatenFood(apiSearchResponse, name, grams);
    }

    private FoodRecord calculateEatenFood(ApiSearchResponse apiSearchResponse, String foodName, float grams) {
        return new FoodRecord(
                UUID.randomUUID().toString(),
                foodName,
                new Date(),
                grams,
                apiSearchResponse.getAverageValueOfNutrient("Energy") / 100 * grams,
                apiSearchResponse.getAverageValueOfNutrient("Total lipid (fat)") / 100 * grams,
                apiSearchResponse.getAverageValueOfNutrient("Protein") / 100 * grams,
                apiSearchResponse.getAverageValueOfNutrient("Carbohydrate, by difference") / 100 * grams
        );
    }
}






