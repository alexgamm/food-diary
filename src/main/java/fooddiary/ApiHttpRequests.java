package fooddiary;

import com.google.gson.Gson;
import fooddiary.model.api.ApiSearchResponse;
import fooddiary.model.api.Food;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.util.Comparator;
import java.util.List;

public class ApiHttpRequests {
    private final static String API_KEY = System.getenv("API_KEY");
    private final static String API_SEARCH_URL = "https://api.nal.usda.gov/fdc/v1/foods/search?";
    private final static Gson GSON = new Gson();

    public ApiSearchResponse findFood(String name, float grams, float energyValue) {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(API_SEARCH_URL)
                .queryString("api_key", API_KEY)
                .queryString("query", name)
                .asJson();
        ApiSearchResponse apiSearchResponse = GSON.fromJson(jsonResponse.getBody().toString(), ApiSearchResponse.class);
        List<Food> foods = apiSearchResponse.getFoods();
        foods.removeIf(food -> food.getNutrientValue("Energy").isEmpty());
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
                            .map(value -> value/ 100 * grams)
                            .orElse(0f),
                    searchedFood
                            .getNutrientValue("Protein").orElse(0f) / 100 * grams,
                    searchedFood.getNutrientValue("Carbohydrate, by difference").orElse(0f) / 100 * grams
            );
        }
        System.out.println(eatenFood.getName() + " Carbs: " + eatenFood.getCarbohydrate() + " Energy: " + eatenFood.getEnergy());
        return new ApiSearchResponse();
    }

}

//public ApiSearchResponse findBasicFood(String name, float grams) {
//
//
//}

//    public ApiSearchResponse findHomeFood (){
//
//    }



