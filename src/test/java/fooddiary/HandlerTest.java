package fooddiary;

import fooddiary.database.DatabaseApi;
import fooddiary.database.FoodRecord;
import fooddiary.database.exception.DatabaseApiException;
import fooddiary.fatsecret.FoodSearch;
import org.junit.jupiter.api.Test;
import yacloud.Event;
import yacloud.Request;
import yacloud.Response;

import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class HandlerTest {

    @Test
    public void invalidCommand_getInvalidCommandResponse() {
        //arrange
        Handler handler = new Handler(null, null);
        Event event = new Event()
                .request(new Request().command("жаренная картошка 100"));
        //act
        Response response = handler.apply(event);
        //assert
        assertEquals("Упс, что-то не так с твоим запросом. Скажи еще раз", response.response().text());
    }

    @Test
    public void addFoodWithoutFoodName_getInvalidCommandResponse() {
        //arrange
        Handler handler = new Handler(null, null);
        Event event = new Event()
                .request(new Request().command("добавить 100 грамм"));
        //act
        Response response = handler.apply(event);
        //assert
        assertEquals("Упс, что-то не так с твоим запросом. Скажи еще раз", response.response().text());
    }

    @Test
    public void addFoodWithoutCaloriesWithoutGramWord_getInvalidCommandResponse() {
        //arrange
        Handler handler = new Handler(null, null);
        Event event = new Event()
                .request(new Request().command("добавить жаренная картошка 100"));
        //act
        Response response = handler.apply(event);
        //assert
        assertEquals("Упс, что-то не так с твоим запросом. Скажи еще раз", response.response().text());
    }

    @Test
    public void addFoodWithoutGramWord_getInvalidCommandResponse() {
        //arrange
        Handler handler = new Handler(null, null);
        Event event = new Event()
                .request(new Request().command("добавить жаренная картошка 100"));
        //act
        Response response = handler.apply(event);
        //assert
        assertEquals("Упс, что-то не так с твоим запросом. Скажи еще раз", response.response().text());
    }

    @Test
    public void addUnknownFoodWithoutCalories_getUnknownDishResponse() {
        //arrange
        FoodSearch foodSearch = mock(FoodSearch.class);
        when(foodSearch.findFood(eq("тапок"), eq(100))).thenReturn(null);
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        Handler handler = new Handler(databaseApi, foodSearch);
        Event event = new Event()
                .request(new Request().command("добавить тапок 100 грамм"));
        //act
        Response response = handler.apply(event);
        //assert
        verifyNoInteractions(databaseApi);
        assertEquals(
                "К сожалению, тапок найти не удалось",
                response.response().text()
        );
    }

    @Test
    public void addFoodWithoutCalories_saveFoodAndSuccessfulResponse() throws DatabaseApiException {
        //arrange
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        FoodSearch foodSearch = mock(FoodSearch.class);
        when(foodSearch.findFood(eq("грибочки"), eq(150f))).thenReturn(new FoodRecord(
                null,
                "грибы",
                null,
                150,
                50,
                0,
                3,
                10
        ));

        Handler handler = new Handler(databaseApi, foodSearch);
        Event event = new Event()
                .request(new Request().command("добавить грибочки 150 грамм"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).addFood(any());
        assertEquals(
                "Успешно добавила грибы или как вы это называете грибочки в дневник питания",
                response.response().text()
        );
    }

    @Test
    public void addUnknownFoodWithCalories_getUnknownFoodResponse() {
        //arrange
        FoodSearch foodSearch = mock(FoodSearch.class);
        when(foodSearch.findFood(eq("тапок"), eq(100f), eq(50f))).thenReturn(null);
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        Handler handler = new Handler(databaseApi, foodSearch);
        Event event = new Event()
                .request(new Request().command("добавить тапок 100 грамм 50 калорий"));
        //act
        Response response = handler.apply(event);
        //assert
        verifyNoInteractions(databaseApi);
        assertEquals(
                "К сожалению, тапок найти не удалось",
                response.response().text()
        );
    }

    @Test
    public void addFoodWithCalories_saveFoodAndSuccessfulResponse() throws DatabaseApiException {
        //arrange
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        FoodSearch foodSearch = mock(FoodSearch.class);
        when(foodSearch.findFood(eq("вафли"), eq(100f), eq(300f))).thenReturn(new FoodRecord(
                null,
                "вафли молочные реки",
                null,
                100,
                300,
                0,
                3,
                10
        ));
        Handler handler = new Handler(databaseApi, foodSearch);
        Event event = new Event()
                .request(new Request().command("добавить вафли 100 грамм 300 калорий"));
        //act
        Response response = handler.apply(event);
        //assert

        assertEquals(
                "Успешно добавила вафли молочные реки или как вы это называете вафли в дневник питания",
                response.response().text()
        );
        verify(databaseApi).addFood(any());
    }


    @Test
    public void findEmptyFoodStatsWithDayMonth_getDatabaseErrorResponse() throws DatabaseApiException {
        //arrange
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), Month.NOVEMBER, 15);
        when(databaseApi.findFoodRecordsByDate(eq(date))).thenReturn(List.of());
        Handler handler = new Handler(databaseApi, null);
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
        Handler handler = new Handler(databaseApi, null);
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
        Handler handler = new Handler(databaseApi, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела 15 ноября"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).findFoodRecordsByDate(date);
        assertEquals(
                "Сегодня вы съели 100 калорий. 0 % белка, 0 % жиров, 100 % углеводов. Сегодня вы ели: каша, ",
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
        Handler handler = new Handler(databaseApi, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела 15 ноября 2022 года"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).findFoodRecordsByDate(date);
        assertEquals(
                "Сегодня вы съели 100 калорий. 0 % белка, 0 % жиров, 100 % углеводов. Сегодня вы ели: каша, ",
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
        Handler handler = new Handler(databaseApi, null);
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
        Handler handler = new Handler(databaseApi, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела сегодня"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).findFoodRecordsByDate(date);
        assertEquals(
                "Сегодня вы съели 100 калорий. 0 % белка, 0 % жиров, 100 % углеводов. Сегодня вы ели: каша, ",
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
        Handler handler = new Handler(databaseApi, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела вчера"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).findFoodRecordsByDate(date);
        assertEquals(
                "Сегодня вы съели 100 калорий. 0 % белка, 0 % жиров, 100 % углеводов. Сегодня вы ели: каша, ",
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
        Handler handler = new Handler(databaseApi, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела позавчера"));
        //act
        Response response = handler.apply(event);
        //assert
        verify(databaseApi).findFoodRecordsByDate(date);
        assertEquals(
                "Сегодня вы съели 100 калорий. 0 % белка, 0 % жиров, 100 % углеводов. Сегодня вы ели: каша, ",
                response.response().text()
        );
    }

    @Test
    public void getEmptyFoodStatsToday_getDatabaseErrorResponse() throws DatabaseApiException {
        //arrange
        DatabaseApi databaseApi = mock(DatabaseApi.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
        when(databaseApi.findFoodRecordsByDate(eq(date))).thenReturn(List.of());
        Handler handler = new Handler(databaseApi, null);
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
        Handler handler = new Handler(databaseApi, null);
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
