package fooddiary;

import fooddiary.database.DatabaseApi;
import fooddiary.database.FoodRecord;
import fooddiary.database.exception.DatabaseApiException;
import fooddiary.fatsecret.FoodSearch;
import fooddiary.utils.FoodRecordUtils;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yacloud.Event;
import yacloud.Response;
import yacloud.TextResponse;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class Handler implements Function<Event, Response> {
    public static final DateTimeFormatter FULL_DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"));
    public static final DateTimeFormatter DATE_MONTH_FORMATTER = DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"));
    private final DatabaseApi databaseApi;
    private final FoodSearch foodSearch;
    private static final String DB_ERROR_RESPONSE = "К сожалению, ничего найти не удалось";
    private static final String SQL_EXCEPTION_RESPONSE = "Что-то база данных шалит. Повтори запрос еще раз";
    private static final String INVALID_COMMAND_RESPONSE = "Упс, что-то не так с твоим запросом. Скажи еще раз";
    private final String kcalPhrasePattern = "калор[а-яА-Я]*";
    private final String gramPhrasePattern = "грам[а-яА-Я]*";
    private final String numberPattern = "^[-+]?[0-9]*\\.?[0-9]+$";

    public Handler() {
        this.databaseApi = new DatabaseApi();
        this.foodSearch = new FoodSearch();
    }

    @Override
    public Response apply(Event event) {
        String command = event.request().command();
        List<String> commandWords = Arrays.stream(command.split(" ")).toList();

        for (String word : commandWords) {
            if (word.equals("добавить")) {
                // попроси дневник питания добавить морковь 100 грамм (60 калорий)
                String foodName = getFoodName(command);
                if (foodName == null) {
                    return getResponse(event, INVALID_COMMAND_RESPONSE);
                }
                float grams = getGrams(commandWords);
                if (grams <= 0) {
                    return getResponse(event, INVALID_COMMAND_RESPONSE);
                }

                for (String commandWord : commandWords) {
                    if (commandWord.matches(kcalPhrasePattern + ".*")) {
                        // попроси дневник питания добавить вафли 100 грамм 60 калорий
                        FoodRecord foodRecord = foodSearch.findFood(foodName, grams, Float.parseFloat(commandWords.get(commandWords.indexOf(commandWord) - 1)));
                        return saveFoodAndgetAddFoodResponse(foodRecord, event, foodName, foodRecord == null ? null : foodRecord.getName());
                    }
                }
                // попроси дневник питания добавить морковь/блины 100 грамм
                FoodRecord foodRecord = foodSearch.findFood(foodName, grams);
                return saveFoodAndgetAddFoodResponse(foodRecord, event, foodName, foodRecord == null ? null : foodRecord.getName());

            } else if (word.equals("сколько")) {
                int dateIdx = 0;

                for (String commandWord : commandWords) {
                    List<FoodRecord> foodRecords;
                    if (commandWord.matches(numberPattern)) {
                        // спроси дневник питания сколько я съела 15 ноября / 25 февраля 2023 года
                        dateIdx = commandWords.indexOf(commandWord);
                        String date = commandWords.get(dateIdx);
                        String month = commandWords.get(dateIdx + 1);
                        String year = null;
                        if (dateIdx + 2 <= commandWords.size() - 1) {
                            year = commandWords.get(dateIdx + 2);
                        }
                        try {
                            foodRecords = databaseApi.findFoodRecordsByDate(parseDate(date, month, year));
                            if (foodRecords.isEmpty()) {
                                return getResponse(event, DB_ERROR_RESPONSE);
                            }
                            return getResponse(event, FoodRecordUtils.getFoodStats(foodRecords));
                        } catch (DatabaseApiException e) {
                            return getResponse(event, SQL_EXCEPTION_RESPONSE);
                        }

                    } else if (commandWord.matches("сегодня|вчера|позавчера")) {
                        // спроси дневник питания сколько я съела сегодня / вчера / позавчера
                        try {
                            foodRecords = databaseApi.findFoodRecordsByDate(getDate(commandWords.get(commandWords.indexOf(commandWord))));
                        } catch (DatabaseApiException e) {
                            return getResponse(event, SQL_EXCEPTION_RESPONSE);
                        }
                        if (foodRecords.isEmpty()) {
                            return getResponse(event, DB_ERROR_RESPONSE);
                        }
                        return getResponse(event, FoodRecordUtils.getFoodStats(foodRecords));
                    }
                }
            } else if (word.equals("удалить")) {
                try {
                    databaseApi.deleteFood();
                    return getResponse(event, "Успешно удалила последнюю запись из дневника питания");
                } catch (DatabaseApiException e) {
                    return getResponse(event, SQL_EXCEPTION_RESPONSE);
                }
            }
        }
        return getResponse(event, INVALID_COMMAND_RESPONSE);
    }

    private Response getResponse(Event event, String responseText) {
        return new Response()
                .session(event.session())
                .version(event.version())
                .response(new TextResponse().text(responseText).end_session(true));
    }

    private LocalDate parseDate(@NotNull String day, @NotNull String month, @Nullable String year) {
        String date = Stream.of(day, month, year).filter(Objects::nonNull).collect(Collectors.joining(" "));
        try {
            return LocalDate.parse(date, FULL_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            MonthDay monthDay = MonthDay.parse(date, DATE_MONTH_FORMATTER);
            return LocalDate.of(LocalDate.now().getYear(), monthDay.getMonth(), monthDay.getDayOfMonth());
        }
    }

    private LocalDate getDate(String day) {
        if (day.equals("сегодня")) {
            return LocalDate.now();
        } else if (day.equals("вчера")) {
            return LocalDate.now().minusDays(1);
        } else if (day.equals("позавчера")) {
            return LocalDate.now().minusDays(2);
        } else {
            return LocalDate.now();
        }
    }

    private String getFoodName(String command) {
        Pattern pattern = Pattern.compile("добавить(?: блюдо)? (.*?) \\d+ грамм");
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) {
            return null;
        }
        String foodName = matcher.group(1).trim();
        if (foodName.equalsIgnoreCase("блюдо")) {
            return null;
        }
        return foodName.length() > 0 ? foodName : null;
    }

    private float getGrams(List<String> commandWords) {
        float grams;
        for (String w : commandWords) {
            if (w.matches(gramPhrasePattern)) {
                return grams = Float.parseFloat(commandWords.get(commandWords.indexOf(w) - 1));
            }
        }
        return 0;
    }

    private Response saveFoodAndgetAddFoodResponse(FoodRecord foodRecord, Event event, String commandFoodName, String fatSecretFoodName) {
        if (foodRecord == null) {
            return getResponse(event, "К сожалению, " + commandFoodName + " найти не удалось");
        }
        try {
            databaseApi.addFood(foodRecord);
        } catch (DatabaseApiException e) {
            return getResponse(event, SQL_EXCEPTION_RESPONSE);
        }
        return getResponse(event, "Успешно добавила " + fatSecretFoodName + " или как вы это называете " + commandFoodName + " в дневник питания");
    }
}



