package fooddiary.utils;

import fooddiary.database.FoodRecord;

import java.util.List;

public class FoodRecordUtils {
    public static String getFoodStats(List<FoodRecord> foodRecords) {
        float overallFat = 0f;
        float overallProtein = 0f;
        float overallCarbs = 0f;
        float overallKcal = 0f;
        String foodNames = "";
        for (FoodRecord foodRecord : foodRecords) {
            overallFat += foodRecord.getTotalFat();
            overallProtein += foodRecord.getProtein();
            overallCarbs += foodRecord.getCarbohydrate();
            overallKcal += foodRecord.getKcal();
            foodNames += foodRecord.getFoodName() + ", ";
        }
        float PfcSum = overallFat + overallCarbs + overallProtein;
        return "Сегодня вы съели " + (long) overallKcal + " калорий. "
                + (long) ((overallProtein / PfcSum) * 100) + " % белка, "
                + (long) ((overallFat / PfcSum) * 100) + " % жиров, "
                + (long) ((overallCarbs / PfcSum) * 100) + " % углеводов. "
                + "Сегодня вы ели: " + foodNames;
    }
}
