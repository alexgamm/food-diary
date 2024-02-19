package fooddiary.command;

import com.github.vdybysov.ydb.exception.YdbClientException;
import fooddiary.PersonRequest;
import fooddiary.database.Database;
import fooddiary.database.FoodRecord;
import fooddiary.fatsecret.FoodSearch;
import fooddiary.utils.Responses;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class AddFoodCommand implements Command {
    private final FoodSearch foodSearch;
    private final Database database;

    @Override
    public String getResponse(PersonRequest personRequest) {
        ParsedFood food;
        try {
            food = ParsedFood.parse(personRequest.getRequest());
        } catch (IllegalArgumentException e) {
            return Responses.invalidCommand();
        }
        FoodRecord foodRecord = null;
        if (food.kcal() != null) {
            // попроси дневник питания добавить вафли 100 грамм 60 калорий
            foodRecord = foodSearch.findFood(food.name(), food.grams(), food.kcal(), personRequest.getPersonId());
        } else {
            foodRecord = foodSearch.findFood(food.name(), food.grams(), personRequest.getPersonId());
        }
        // попроси дневник питания добавить морковь/блины 100 грамм
        if (foodRecord == null) {
            return Responses.foodNotFound(food.name());
        }
        try {
            database.addFood(foodRecord);
        } catch (YdbClientException e) {
            return Responses.dbError();
        }
        return Responses.foundAndAddFood(foodRecord.name(), food.name());
    }

    @Override
    public boolean isRelevant(String request) {
        // попроси дневник питания добавить морковь 100 грамм (60 калорий)
        return request.startsWith("добавить");
    }

    private record ParsedFood(@NotNull String name, @NotNull Float grams, @Nullable Float kcal) {
        static ParsedFood parse(String command) throws IllegalArgumentException {
            Pattern pattern = Pattern.compile("добавить (.*) (\\d+) грам[а-я]*(?: (\\d+) калор[а-я]*)?");
            Matcher matcher = pattern.matcher(command);
            if (!matcher.find() || matcher.groupCount() < 2) {
                throw new IllegalArgumentException();
            }
            String foodName = matcher.group(1).trim();
            float grams = Float.parseFloat(matcher.group(2).trim());
            if (foodName.isEmpty() || grams <= 0) {
                throw new IllegalArgumentException();
            }
            String kcalGroup = matcher.groupCount() > 2 ? matcher.group(3) : null;
            Float kcal = null;
            if (kcalGroup != null) {
                kcal = Float.parseFloat(kcalGroup.trim());
                if (kcal <= 0) {
                    throw new IllegalArgumentException();
                }
            }
            return new ParsedFood(foodName, grams, kcal);
        }
    }
}
