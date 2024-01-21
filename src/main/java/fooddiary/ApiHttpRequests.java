package fooddiary;

import fooddiary.model.api.ApiSearchResponse;
import fooddiary.model.api.DataType;
import fooddiary.model.api.Food;

import java.util.Comparator;
import java.util.List;

public class ApiHttpRequests {
    private final static String API_KEY = System.getenv("API_KEY");
    private final UsdaApiClient usdaApiClient = new UsdaApiClient(API_KEY);

    public FoodDto findFood(String name, float grams, float energyValue) {
        ApiSearchResponse apiSearchResponse = usdaApiClient.search(name, null);
        List<Food>foods = apiSearchResponse.getFoodsWithEnergy();
        Food searchedFood = foods.stream()
                .min(Comparator.comparing(food -> Math.abs(
                        food.getNutrientValue("Energy").orElse(0f) - energyValue
                )))
                .orElse(null);
        FoodDto eatenFood = null;
        if (searchedFood != null) {
            eatenFood = new FoodDto(
                    name,
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
        System.out.println(eatenFood.getName() + " Carbs: " + eatenFood.getCarbohydrate() + " Energy: " + eatenFood.getEnergy());
        return eatenFood;
    }

    public FoodDto findBasicFood(String name, float grams) {
        ApiSearchResponse apiSearchResponse = usdaApiClient.search(name, DataType.Foundation);
        FoodDto eatenFood = new FoodDto(
                name,
                apiSearchResponse.getAverageValueOfNutrient("Energy") / 100 * grams,
                apiSearchResponse.getAverageValueOfNutrient("Total lipid (fat)") / 100 * grams,
                apiSearchResponse.getAverageValueOfNutrient("Protein") / 100 * grams,
                apiSearchResponse.getAverageValueOfNutrient("Carbohydrate, by difference") / 100 * grams
        );
        System.out.println(eatenFood.getName() + " Carbs: " + eatenFood.getCarbohydrate() + " Energy: " + eatenFood.getEnergy() + " Protein: " + eatenFood.getProtein());
        return eatenFood;
    }

    //    public ApiSearchResponse findHomeFood (){
//
//    }
}






