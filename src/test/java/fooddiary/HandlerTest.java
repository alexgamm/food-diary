package fooddiary;

import fooddiary.database.DatabaseApi;
import fooddiary.database.FoodRecord;
import fooddiary.database.exception.DatabaseApiException;
import fooddiary.google.api.TranslationRequest;
import fooddiary.usda.api.ApiHttpRequests;
import fooddiary.usda.api.UsdaApiClient;
import fooddiary.usda.api.model.ApiSearchResponse;
import fooddiary.usda.api.model.Food;
import fooddiary.usda.api.model.FoodNutrient;
import org.junit.jupiter.api.Test;
import yacloud.Event;
import yacloud.Request;
import yacloud.Response;

import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static fooddiary.usda.api.model.DataType.Foundation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class HandlerTest {

    @Test
    public void invalidCommand_getInvalidCommandResponse() {
        //arrange
        Handler handler = new Handler(null, null, null);
        Event event = new Event()
                .request(new Request().command("блюдо жаренная картошка 100"));
        //act
        Response response = handler.apply(event);
        //assert
        assertEquals("Упс, что-то не так с твоим запросом. Скажи еще раз", response.response().text());
    }

    @Test
    public void addFoodWithoutFoodName_getInvalidCommandResponse() {
        //arrange
        Handler handler = new Handler(null, null, null);
        Event event = new Event()
                .request(new Request().command("добавить блюдо 100 грамм"));
        //act
        Response response = handler.apply(event);
        //assert
        assertEquals("Упс, что-то не так с твоим запросом. Скажи еще раз", response.response().text());
    }

    @Test
    public void addFoodTranslationFailed_getTranslationErrorResponse() {
        //arrange
        TranslationRequest translationRequest = mock(TranslationRequest.class);
        when(translationRequest.translate(anyString())).thenThrow(new RuntimeException());
        Handler handler = new Handler(null, null, translationRequest);
        Event event = new Event()
                .request(new Request().command("добавить блюдо жаренная картошка 100 грамм"));
        //act
        Response response = handler.apply(event);
        //assert
        assertEquals("Что-то гугл переводчик шалит. Повтори запрос еще раз", response.response().text());
    }

    @Test
    public void addDishWithoutGramWord_getInvalidCommandResponse() {
        //arrange
        TranslationRequest translationRequest = mock(TranslationRequest.class);
        Handler handler = new Handler(null, null, translationRequest);
        Event event = new Event()
                .request(new Request().command("добавить блюдо жаренная картошка 100"));
        //act
        Response response = handler.apply(event);
        //assert
        assertEquals("Упс, что-то не так с твоим запросом. Скажи еще раз", response.response().text());
    }

    @Test
    public void addUnknownDish_saveFoodAndGetUnknownDishResponse() {
        //arrange
        TranslationRequest translationRequest = mock(TranslationRequest.class);
        when(translationRequest.translate(eq("тапок"))).thenReturn("slipper");
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        UsdaApiClient usdaApiClient = mock(UsdaApiClient.class);
        when(usdaApiClient.search(eq("slipper"), eq(null))).thenReturn(new ApiSearchResponse(List.of()));
        ApiHttpRequests apiHttpRequests = new ApiHttpRequests(usdaApiClient);
        Handler handler = new Handler(databaseApi, apiHttpRequests, translationRequest);
        Event event = new Event()
                .request(new Request().command("добавить блюдо тапок 100 грамм"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).addFood(any());
        assertEquals(
                "К сожалению, slipper или как вы называете тапок нет в моём списке",
                response.response().text()
        );
    }

    @Test
    public void addDish_saveFoodAndSuccessfulResponse() {
        //arrange
        TranslationRequest translationRequest = mock(TranslationRequest.class);
        when(translationRequest.translate(eq("куриный салат"))).thenReturn("chicken salad");
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        UsdaApiClient usdaApiClient = mock(UsdaApiClient.class);
        List<Food> foods = List.of(new Food(List.of(
                new FoodNutrient("Carbohydrate, by difference", 20),
                new FoodNutrient("Energy", 100))
        ));
        when(usdaApiClient.search(eq("chicken salad"), eq(null))).thenReturn(new ApiSearchResponse(foods));
        ApiHttpRequests apiHttpRequests = new ApiHttpRequests(usdaApiClient);
        Handler handler = new Handler(databaseApi, apiHttpRequests, translationRequest);
        Event event = new Event()
                .request(new Request().command("добавить блюдо куриный салат 100 грамм"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).addFood(any());
        assertEquals(
                "Успешно добавила куриный салат в дневник питания",
                response.response().text()
        );
    }

    @Test
    public void addUnknownGroceryFood_saveFoodAndGetUnknownGroceryFoodResponse() {
        //arrange
        TranslationRequest translationRequest = mock(TranslationRequest.class);
        when(translationRequest.translate(eq("любятово"))).thenReturn("lubyatovo");
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        UsdaApiClient usdaApiClient = mock(UsdaApiClient.class);
        when(usdaApiClient.search(eq("lubyatovo"), eq(null))).thenReturn(new ApiSearchResponse(List.of()));
        ApiHttpRequests apiHttpRequests = new ApiHttpRequests(usdaApiClient);
        Handler handler = new Handler(databaseApi, apiHttpRequests, translationRequest);
        Event event = new Event()
                .request(new Request().command("добавить любятово 100 грамм 200 калорий"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).addFood(any());
        assertEquals(
                "К сожалению, lubyatovo или как вы называете любятово нет в моём списке",
                response.response().text()
        );
    }

    @Test
    public void addGroceryFood_saveFoodAndSuccessfulResponse() {
        //arrange
        TranslationRequest translationRequest = mock(TranslationRequest.class);
        when(translationRequest.translate(eq("вафли"))).thenReturn("wafers");
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        UsdaApiClient usdaApiClient = mock(UsdaApiClient.class);
        List<Food> foods = List.of(new Food(List.of(
                new FoodNutrient("Carbohydrate, by difference", 50),
                new FoodNutrient("Energy", 305))
        ));
        when(usdaApiClient.search(eq("wafers"), eq(null))).thenReturn(new ApiSearchResponse(foods));
        ApiHttpRequests apiHttpRequests = new ApiHttpRequests(usdaApiClient);
        Handler handler = new Handler(databaseApi, apiHttpRequests, translationRequest);
        Event event = new Event()
                .request(new Request().command("добавить вафли 100 грамм 300 калорий"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).addFood(any());
        assertEquals(
                "Успешно добавила вафли в дневник питания",
                response.response().text()
        );
    }

    @Test
    public void addUnknownBasicFood_saveFoodAndGetUnknownBasicFoodResponse() {
        //arrange
        TranslationRequest translationRequest = mock(TranslationRequest.class);
        when(translationRequest.translate(eq("гриб"))).thenReturn("mushroom");
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        UsdaApiClient usdaApiClient = mock(UsdaApiClient.class);
        when(usdaApiClient.search(eq("mushroom"), eq(Foundation))).thenReturn(new ApiSearchResponse(List.of()));
        ApiHttpRequests apiHttpRequests = new ApiHttpRequests(usdaApiClient);
        Handler handler = new Handler(databaseApi, apiHttpRequests, translationRequest);
        Event event = new Event()
                .request(new Request().command("добавить гриб 100 грамм"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).addFood(any());
        assertEquals(
                "К сожалению, mushroom или как вы называете гриб нет в моём списке",
                response.response().text()
        );
    }

    @Test
    public void addBasicFood_saveFoodAndSuccessfulResponse() {
        //arrange
        TranslationRequest translationRequest = mock(TranslationRequest.class);
        when(translationRequest.translate(eq("яйцо"))).thenReturn("egg");
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        UsdaApiClient usdaApiClient = mock(UsdaApiClient.class);
        List<Food> foods = List.of(new Food(List.of(
                new FoodNutrient("Carbohydrate, by difference", 0),
                new FoodNutrient("Energy", 90))
        ));
        when(usdaApiClient.search(eq("egg"), eq(Foundation))).thenReturn(new ApiSearchResponse(foods));
        ApiHttpRequests apiHttpRequests = new ApiHttpRequests(usdaApiClient);
        Handler handler = new Handler(databaseApi, apiHttpRequests, translationRequest);
        Event event = new Event()
                .request(new Request().command("добавить яйцо 100 грамм"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).addFood(any());
        assertEquals(
                "Успешно добавила яйцо в дневник питания",
                response.response().text()
        );
    }

    @Test
    public void findEmptyFoodStatsWithDayMonth_getDatabaseErrorResponse() throws DatabaseApiException {
        //arrange
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), Month.NOVEMBER, 15);
        when(databaseApi.findFoodRecordsByDate(eq(date))).thenReturn(List.of());
        Handler handler = new Handler(databaseApi, null, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела 15 ноября"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).findFoodRecordsByDate(date);
        assertEquals(
                "К сожалению, ничего найти не удалось",
                response.response().text()
        );
    }

    @Test
    public void getEmptyFoodStatsWithFullDate_getDatabaseErrorResponse() throws DatabaseApiException {
        //arrange
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        LocalDate date = LocalDate.of(2022, Month.NOVEMBER, 15);
        when(databaseApi.findFoodRecordsByDate(eq(date))).thenReturn(List.of());
        Handler handler = new Handler(databaseApi, null, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела 15 ноября 2022 года"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).findFoodRecordsByDate(date);
        assertEquals(
                "К сожалению, ничего найти не удалось",
                response.response().text()
        );
    }

    @Test
    public void findFoodStatsWithDayMonth_getSuccessfulResponse() throws DatabaseApiException {
        //arrange
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), Month.NOVEMBER, 15);
        List<FoodRecord> foodRecords = List.of(new FoodRecord(
                UUID.randomUUID().toString(),
                "каша",
                new Date(),
                100,
                100,
                0,
                0,
                1
        ));
        when(databaseApi.findFoodRecordsByDate(eq(date))).thenReturn(foodRecords);
        Handler handler = new Handler(databaseApi, null, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела 15 ноября"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).findFoodRecordsByDate(date);
        assertEquals(
                "Сегодня вы съели 100 калорий. 0 процентов белка, 0 процентов жиров, 100 процентов углеводов. Сегодня вы ели: каша, ",
                response.response().text()
        );
    }

    @Test
    public void findFoodStatsWithFullDate_getSuccessfulResponse() throws DatabaseApiException {
        //arrange
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        LocalDate date = LocalDate.of(2022, Month.NOVEMBER, 15);
        List<FoodRecord> foodRecords = List.of(new FoodRecord(
                UUID.randomUUID().toString(),
                "каша",
                new Date(),
                100,
                100,
                0,
                0,
                1
        ));
        when(databaseApi.findFoodRecordsByDate(eq(date))).thenReturn(foodRecords);
        Handler handler = new Handler(databaseApi, null, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела 15 ноября 2022 года"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).findFoodRecordsByDate(date);
        assertEquals(
                "Сегодня вы съели 100 калорий. 0 процентов белка, 0 процентов жиров, 100 процентов углеводов. Сегодня вы ели: каша, ",
                response.response().text()
        );
    }

    @Test
    public void findFoodStatsWithDateMonthWhenDatabaseApiException_getDatabaseApiExceptionResponse() throws DatabaseApiException {
        //arrange
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), Month.NOVEMBER, 15);
        when(databaseApi.findFoodRecordsByDate(eq(date))).thenThrow(DatabaseApiException.class);
        String expectedMessage = "Что-то база данных шалит. Повтори запрос еще раз";
        Handler handler = new Handler(databaseApi, null, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела 15 ноября"));
        //act
        Response response = handler.apply(event);
        //assert
        assertEquals(
                expectedMessage,
                response.response().text()
        );
    }

    @Test
    public void findFoodStatsToday_getSuccessfulResponse() throws DatabaseApiException {
        //arrange
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
        List<FoodRecord> foodRecords = List.of(new FoodRecord(
                UUID.randomUUID().toString(),
                "каша",
                new Date(),
                100,
                100,
                0,
                0,
                1
        ));
        when(databaseApi.findFoodRecordsByDate(eq(date))).thenReturn(foodRecords);
        Handler handler = new Handler(databaseApi, null, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела сегодня"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).findFoodRecordsByDate(date);
        assertEquals(
                "Сегодня вы съели 100 калорий. 0 процентов белка, 0 процентов жиров, 100 процентов углеводов. Сегодня вы ели: каша, ",
                response.response().text()
        );
    }

    @Test
    public void findFoodStatsYesterday_getSuccessfulResponse() throws DatabaseApiException {
        //arrange
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().minusDays(1).getDayOfMonth());
        List<FoodRecord> foodRecords = List.of(new FoodRecord(
                UUID.randomUUID().toString(),
                "каша",
                new Date(),
                100,
                100,
                0,
                0,
                1
        ));
        when(databaseApi.findFoodRecordsByDate(eq(date))).thenReturn(foodRecords);
        Handler handler = new Handler(databaseApi, null, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела вчера"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).findFoodRecordsByDate(date);
        assertEquals(
                "Сегодня вы съели 100 калорий. 0 процентов белка, 0 процентов жиров, 100 процентов углеводов. Сегодня вы ели: каша, ",
                response.response().text()
        );
    }

    @Test
    public void findFoodStatsTheDayBeforeYesterday_getSuccessfulResponse() throws DatabaseApiException {
        //arrange
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().minusDays(2).getDayOfMonth());
        List<FoodRecord> foodRecords = List.of(new FoodRecord(
                UUID.randomUUID().toString(),
                "каша",
                new Date(),
                100,
                100,
                0,
                0,
                1
        ));
        when(databaseApi.findFoodRecordsByDate(eq(date))).thenReturn(foodRecords);
        Handler handler = new Handler(databaseApi, null, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела позавчера"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).findFoodRecordsByDate(date);
        assertEquals(
                "Сегодня вы съели 100 калорий. 0 процентов белка, 0 процентов жиров, 100 процентов углеводов. Сегодня вы ели: каша, ",
                response.response().text()
        );
    }

    @Test
    public void getEmptyFoodStatsToday_getDatabaseErrorResponse() throws DatabaseApiException {
        //arrange
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
        when(databaseApi.findFoodRecordsByDate(eq(date))).thenReturn(List.of());
        Handler handler = new Handler(databaseApi, null, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела сегодня"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).findFoodRecordsByDate(date);
        assertEquals(
                "К сожалению, ничего найти не удалось",
                response.response().text()
        );
    }

    @Test
    public void findFoodStatsTodayWhenDatabaseApiException_getDatabaseApiExceptionResponse() throws DatabaseApiException {
        //arrange
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
        when(databaseApi.findFoodRecordsByDate(eq(date))).thenThrow(new DatabaseApiException());
        String expectedMessage = "Что-то база данных шалит. Повтори запрос еще раз";
        Handler handler = new Handler(databaseApi, null, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела сегодня"));
        //act
        Response response = handler.apply(event);
        //assert
        assertEquals(
                expectedMessage,
                response.response().text()
        );
    }


}
