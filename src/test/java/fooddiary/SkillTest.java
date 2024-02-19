package fooddiary;

import com.github.vdybysov.ydb.exception.YdbClientException;
import fooddiary.database.Database;
import fooddiary.database.FoodRecord;
import fooddiary.fatsecret.FoodSearch;
import org.junit.jupiter.api.Test;
import yacloud.Event;
import yacloud.Request;
import yacloud.Response;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SkillTest {

    @Test
    public void invalidCommand_getInvalidCommandResponse() {
        //arrange
        Skill skill = new Skill(null, null);
        Event event = new Event()
                .request(new Request().command("жаренная картошка 100"));
        //act
        Response response = skill.getResponse(event);
        //assert
        assertEquals("Упс, что-то не так с твоим запросом. Скажи еще раз", response.response().text());
    }

    @Test
    public void addFoodWithoutFoodName_getInvalidCommandResponse() {
        //arrange
        Skill skill = new Skill(null, null);
        Event event = new Event()
                .request(new Request().command("добавить 100 грамм"));
        //act
        Response response = skill.getResponse(event);
        //assert
        assertEquals("Упс, что-то не так с твоим запросом. Скажи еще раз", response.response().text());
    }

    @Test
    public void addFoodWithoutCaloriesWithoutGramWord_getInvalidCommandResponse() {
        //arrange
        Skill skill = new Skill(null, null);
        Event event = new Event()
                .request(new Request().command("добавить жаренная картошка 100"));
        //act
        Response response = skill.getResponse(event);
        //assert
        assertEquals("Упс, что-то не так с твоим запросом. Скажи еще раз", response.response().text());
    }

    @Test
    public void addFoodWithoutGramWord_getInvalidCommandResponse() {
        //arrange
        Skill skill = new Skill(null, null);
        Event event = new Event()
                .request(new Request().command("добавить жаренная картошка 100"));
        //act
        Response response = skill.getResponse(event);
        //assert
        assertEquals("Упс, что-то не так с твоим запросом. Скажи еще раз", response.response().text());
    }

    @Test
    public void addUnknownFoodWithoutCalories_getUnknownDishResponse() {
        //arrange
        FoodSearch foodSearch = mock(FoodSearch.class);
        when(foodSearch.findFood(eq("тапок"), eq(100))).thenReturn(null);
        Database database = mock(Database.class);
        Skill skill = new Skill(database, foodSearch);
        Event event = new Event()
                .request(new Request().command("добавить тапок 100 грамм"));
        //act
        Response response = skill.getResponse(event);
        //assert
        verifyNoInteractions(database);
        assertEquals(
                "К сожалению, тапок найти не удалось",
                response.response().text()
        );
    }

    @Test
    public void addFoodWithoutCalories_saveFoodAndSuccessfulResponse() throws YdbClientException {
        //arrange
        Database database = mock(Database.class);
        FoodSearch foodSearch = mock(FoodSearch.class);
        when(foodSearch.findFood(eq("грибочки"), eq(150f))).thenReturn(new FoodRecord(
                null,
                "грибы",
                null,
                150f,
                50f,
                0f,
                3f,
                10f
        ));

        Skill skill = new Skill(database, foodSearch);
        Event event = new Event()
                .request(new Request().command("добавить грибочки 150 грамм"));
        //act
        Response response = skill.getResponse(event);
        //assert
        verify(database).addFood(any());
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
        Database database = mock(Database.class);
        Skill skill = new Skill(database, foodSearch);
        Event event = new Event()
                .request(new Request().command("добавить тапок 100 грамм 50 калорий"));
        //act
        Response response = skill.getResponse(event);
        //assert
        verifyNoInteractions(database);
        assertEquals(
                "К сожалению, тапок найти не удалось",
                response.response().text()
        );
    }

    @Test
    public void addFoodWithCalories_saveFoodAndSuccessfulResponse() throws YdbClientException {
        //arrange
        Database database = mock(Database.class);
        FoodSearch foodSearch = mock(FoodSearch.class);
        when(foodSearch.findFood(eq("вафли"), eq(100f), eq(300f))).thenReturn(new FoodRecord(
                null,
                "вафли молочные реки",
                null,
                100f,
                300f,
                0f,
                3f,
                10f
        ));
        Skill skill = new Skill(database, foodSearch);
        Event event = new Event()
                .request(new Request().command("добавить вафли 100 грамм 300 калорий"));
        //act
        Response response = skill.getResponse(event);
        //assert

        assertEquals(
                "Успешно добавила вафли молочные реки или как вы это называете вафли в дневник питания",
                response.response().text()
        );
        verify(database).addFood(any());
    }


    @Test
    public void findEmptyFoodStatsWithDayMonth_getDatabaseErrorResponse() throws YdbClientException {
        //arrange
        Database database = mock(Database.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), Month.NOVEMBER, 15);
        LocalDate actualDate = date.isAfter(LocalDate.now()) ? date.minusYears(1) : date;
        when(database.findFoodRecordsByDate(eq(actualDate))).thenReturn(List.of());
        Skill skill = new Skill(database, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела 15 ноября"));
        //act
        Response response = skill.getResponse(event);
        //assert
        verify(database).findFoodRecordsByDate(actualDate);
        assertEquals(
                "В этот день вы не вели дневник",
                response.response().text()
        );
    }

    @Test
    public void getEmptyFoodStatsWithFullDate_getDatabaseErrorResponse() throws YdbClientException {
        //arrange
        Database database = mock(Database.class);
        LocalDate date = LocalDate.of(2022, Month.NOVEMBER, 15);
        when(database.findFoodRecordsByDate(eq(date))).thenReturn(List.of());
        Skill skill = new Skill(database, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела 15 ноября 2022 года"));
        //act
        Response response = skill.getResponse(event);
        //assert
        verify(database).findFoodRecordsByDate(date);
        assertEquals(
                "В этот день вы не вели дневник",
                response.response().text()
        );
    }

    @Test
    public void findFoodStatsWithDayMonth_getSuccessfulResponse() throws YdbClientException {
        //arrange
        Database database = mock(Database.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), Month.NOVEMBER, 15);
        List<FoodRecord> foodRecords = List.of(new FoodRecord(
                UUID.randomUUID().toString(),
                "каша",
                Instant.now(),
                100f,
                100f,
                0f,
                0f,
                1f
        ));
        LocalDate actualDate = date.isAfter(LocalDate.now()) ? date.minusYears(1) : date;
        when(database.findFoodRecordsByDate(eq(actualDate))).thenReturn(foodRecords);
        Skill skill = new Skill(database, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела 15 ноября"));
        //act
        Response response = skill.getResponse(event);
        //assert
        verify(database).findFoodRecordsByDate(actualDate);
        assertEquals(
                "Вы съели 100 калорий. 0 % белка, 0 % жиров, 100 % углеводов. Вы ели: каша",
                response.response().text()
        );
    }

    @Test
    public void findFoodStatsWithFullDate_getSuccessfulResponse() throws YdbClientException {
        //arrange
        Database database = mock(Database.class);
        LocalDate date = LocalDate.of(2022, Month.NOVEMBER, 15);
        List<FoodRecord> foodRecords = List.of(new FoodRecord(
                UUID.randomUUID().toString(),
                "каша",
                Instant.now(),
                100f,
                100f,
                0f,
                0f,
                1f
        ));
        when(database.findFoodRecordsByDate(eq(date))).thenReturn(foodRecords);
        Skill skill = new Skill(database, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела 15 ноября 2022 года"));
        //act
        Response response = skill.getResponse(event);
        //assert
        verify(database).findFoodRecordsByDate(date);
        assertEquals(
                "Вы съели 100 калорий. 0 % белка, 0 % жиров, 100 % углеводов. Вы ели: каша",
                response.response().text()
        );
    }

    @Test
    public void findFoodStatsWithDateMonthWhenYdbClientException_getYdbClientExceptionResponse() throws YdbClientException {
        //arrange
        Database database = mock(Database.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), Month.NOVEMBER, 15);
        LocalDate actualDate = date.isAfter(LocalDate.now()) ? date.minusYears(1) : date;
        when(database.findFoodRecordsByDate(eq(actualDate))).thenThrow(YdbClientException.class);
        String expectedMessage = "База данных сейчас недоступна. Повторите попытку позже";
        Skill skill = new Skill(database, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела 15 ноября"));
        //act
        Response response = skill.getResponse(event);
        //assert
        assertEquals(
                expectedMessage,
                response.response().text()
        );
    }

    @Test
    public void findFoodStatsToday_getSuccessfulResponse() throws YdbClientException {
        //arrange
        Database database = mock(Database.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
        List<FoodRecord> foodRecords = List.of(new FoodRecord(
                UUID.randomUUID().toString(),
                "каша",
                Instant.now(),
                100f,
                100f,
                0f,
                0f,
                1f
        ));
        when(database.findFoodRecordsByDate(eq(date))).thenReturn(foodRecords);
        Skill skill = new Skill(database, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела сегодня"));
        //act
        Response response = skill.getResponse(event);
        //assert
        verify(database).findFoodRecordsByDate(date);
        assertEquals(
                "Вы съели 100 калорий. 0 % белка, 0 % жиров, 100 % углеводов. Вы ели: каша",
                response.response().text()
        );
    }

    @Test
    public void findFoodStatsYesterday_getSuccessfulResponse() throws YdbClientException {
        //arrange
        Database database = mock(Database.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().minusDays(1).getDayOfMonth());
        List<FoodRecord> foodRecords = List.of(new FoodRecord(
                UUID.randomUUID().toString(),
                "каша",
                Instant.now(),
                100f,
                100f,
                0f,
                0f,
                1f
        ));
        when(database.findFoodRecordsByDate(eq(date))).thenReturn(foodRecords);
        Skill skill = new Skill(database, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела вчера"));
        //act
        Response response = skill.getResponse(event);
        //assert
        verify(database).findFoodRecordsByDate(date);
        assertEquals(
                "Вы съели 100 калорий. 0 % белка, 0 % жиров, 100 % углеводов. Вы ели: каша",
                response.response().text()
        );
    }

    @Test
    public void findFoodStatsTheDayBeforeYesterday_getSuccessfulResponse() throws YdbClientException {
        //arrange
        Database database = mock(Database.class);
        LocalDate date = LocalDate.now().minusDays(2);
        List<FoodRecord> foodRecords = List.of(new FoodRecord(
                UUID.randomUUID().toString(),
                "каша",
                Instant.now(),
                100f,
                100f,
                0f,
                0f,
                1f
        ));
        when(database.findFoodRecordsByDate(eq(date))).thenReturn(foodRecords);
        Skill skill = new Skill(database, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела позавчера"));
        //act
        Response response = skill.getResponse(event);
        //assert
        verify(database).findFoodRecordsByDate(date);
        assertEquals(
                "Вы съели 100 калорий. 0 % белка, 0 % жиров, 100 % углеводов. Вы ели: каша",
                response.response().text()
        );
    }

    @Test
    public void getEmptyFoodStatsToday_getDatabaseErrorResponse() throws YdbClientException {
        //arrange
        Database database = mock(Database.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
        when(database.findFoodRecordsByDate(eq(date))).thenReturn(List.of());
        Skill skill = new Skill(database, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела сегодня"));
        //act
        Response response = skill.getResponse(event);
        //assert
        verify(database).findFoodRecordsByDate(date);
        assertEquals(
                "В этот день вы не вели дневник",
                response.response().text()
        );
    }

    @Test
    public void findFoodStatsTodayWhenYdbClientException_getYdbClientExceptionResponse() throws YdbClientException {
        //arrange
        Database database = mock(Database.class);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
        when(database.findFoodRecordsByDate(eq(date))).thenThrow(new YdbClientException(null));
        String expectedMessage = "База данных сейчас недоступна. Повторите попытку позже";
        Skill skill = new Skill(database, null);
        Event event = new Event()
                .request(new Request().command("сколько я съела сегодня"));
        //act
        Response response = skill.getResponse(event);
        //assert
        assertEquals(
                expectedMessage,
                response.response().text()
        );
    }


}
