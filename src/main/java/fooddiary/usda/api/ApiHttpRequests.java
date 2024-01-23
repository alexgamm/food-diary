package fooddiary.usda.api;

import fooddiary.FoodDto;
import fooddiary.usda.api.model.ApiSearchResponse;
import fooddiary.usda.api.model.DataType;
import fooddiary.usda.api.model.Food;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ApiHttpRequests {
    private final static String API_KEY = System.getenv("API_KEY");
    private final UsdaApiClient usdaApiClient = new UsdaApiClient(API_KEY);

    public FoodDto findFood(String name, float grams, float energyValue) {
        ApiSearchResponse apiSearchResponse = usdaApiClient.search(name, null);
        List<Food> foods = apiSearchResponse.getFoodsWithEnergy();
        Food searchedFood = foods.stream()
                .min(Comparator.comparing(food -> Math.abs(
                        food.getNutrientValue("Energy").orElse(0f) - energyValue
                )))
                .orElse(null);

        if (searchedFood == null) {
            return new FoodDto(UUID.randomUUID().toString(), name, new Date(), grams, 0, 0, 0, 0);
        }
        return new FoodDto(
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

    public FoodDto findBasicFood(String name, float grams) {
        ApiSearchResponse apiSearchResponse = usdaApiClient.search(name, DataType.Foundation);
        return calculateEatenFood(apiSearchResponse, name, grams);
    }

    public FoodDto findHomeFood(String name, float grams) {
        ApiSearchResponse apiSearchResponse = usdaApiClient.search(name, null);
        return calculateEatenFood(apiSearchResponse, name, grams);
    }

    private FoodDto calculateEatenFood(ApiSearchResponse apiSearchResponse, String foodName, float grams) {
        return new FoodDto(
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






