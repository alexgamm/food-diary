package fooddiary.utils;

import fooddiary.database.FoodRecord;

import java.util.ArrayList;
import java.util.List;

public record EatenFoodStats(
        int fatPercentage,
        int proteinPercentage,
        int carbsPercentage,
        int overallKcal,
        List<String> foodNames
) {
    public static EatenFoodStats of(List<FoodRecord> foodRecords) {
        float overallFat = 0f;
        float overallProtein = 0f;
        float overallCarbs = 0f;
        float overallKcal = 0f;
        List<String> foodNames = new ArrayList<>();
        for (FoodRecord foodRecord : foodRecords) {
            overallFat += foodRecord.fat();
            overallProtein += foodRecord.protein();
            overallCarbs += foodRecord.carbohydrate();
            overallKcal += foodRecord.kcal();
            foodNames.add(foodRecord.name());
        }
        float pfcSum = overallFat + overallCarbs + overallProtein;
        return new EatenFoodStats(
                getRoundedPercent(overallFat, pfcSum),
                getRoundedPercent(overallProtein, pfcSum),
                getRoundedPercent(overallCarbs, pfcSum),
                Math.round(overallKcal),
                foodNames
        );
    }

    static int getRoundedPercent(float value, float total) {
        return Math.round(value / total * 100);
    }
}
