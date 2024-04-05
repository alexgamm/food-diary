package fooddiary.utils;

import static java.text.MessageFormat.format;

public interface Responses {

    static String foodRecordsNotFound() {
        return "В этот день вы не вели дневник";
    }

    static String dbError() {
        return "База данных сейчас недоступна. Повторите попытку позже";
    }

    static String invalidCommand() {
        return "Упс, что-то не так с твоим запросом. Скажи еще раз";
    }

    static String foodNotFound(String foodName) {
        return format("К сожалению, {0} найти не удалось", foodName);
    }

    static String foundAndAddFood(String apiFoodName, String commandFoodName) {
        return format(
                "Успешно добавила {0} или как вы это называете {1} в дневник питания",
                apiFoodName,
                commandFoodName
        );
    }

    static String successfulFoodDelete(String foodName) {
        return format("Успешно удалила {0} из дневника питания", foodName);
    }

    static String unsuccessfulFoodDelete() {
        return "К сожалению, найти последнюю запись не удалось";
    }

    static String eatenFoodStats(EatenFoodStats eatenFoodStats) {
        return format(
                "Вы съели {0,number,#} калорий. " +
                        "{1,number,#} % белка, {2,number,#} % жиров, {3,number,#} % углеводов. Вы ели: {4}",
                eatenFoodStats.overallKcal(),
                eatenFoodStats.proteinPercentage(),
                eatenFoodStats.fatPercentage(),
                eatenFoodStats.carbsPercentage(),
                String.join(", ", eatenFoodStats.foodNames())
        );
    }
}
