package fooddiary.command;

import com.github.vdybysov.ydb.exception.YdbClientException;
import fooddiary.database.FoodRecord;
import fooddiary.model.PersonRequest;
import fooddiary.utils.DateParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetFoodStatsCommandTest extends CommandTest {
    @BeforeAll
    public void setup() {
        command = new GetFoodStatsCommand(database);
    }

    @Test
    public void getEmptyFoodStatsWithDayMonth_getFoodRecordsNotFoundResponse() throws YdbClientException {
        //arrange
        PersonRequest personRequest = personRequest("сколько я съела 15 ноября");
        DateParser dateParser = new DateParser(Clock.systemUTC());
        LocalDate date = dateParser.parse(personRequest.request());
        when(database.findFoodRecordsByDate(eq(date), eq(personRequest.getPersonId()))).thenReturn(List.of());
        //act
        String response = command.getResponse(personRequest);
        //assert
        verify(database).findFoodRecordsByDate(date, personRequest.getPersonId());
        assertEquals("В этот день вы не вели дневник", response);
    }

    @Test
    public void getFoodStatsTheDayBeforeYesterday_getEatenFoodStatsResponse() throws YdbClientException {
        //arrange
        PersonRequest personRequest = personRequest("сколько я съела позавчера");
        LocalDate date = LocalDate.now().minusDays(2);
        List<FoodRecord> foodRecords = List.of(new FoodRecord(
                UUID.randomUUID().toString(),
                personRequest.getPersonId(),
                "каша",
                Instant.now(),
                100f,
                100f,
                0f,
                0f,
                1f
        ));
        when(database.findFoodRecordsByDate(eq(date), eq(personRequest.getPersonId()))).thenReturn(foodRecords);
        //act
        String response = command.getResponse(personRequest);
        //assert
        verify(database).findFoodRecordsByDate(date, personRequest.getPersonId());
        assertEquals(
                "Вы съели 100 калорий. 0 % белка, 0 % жиров, 100 % углеводов. Вы ели: каша",
                response
        );
    }

//    @Test
//    public void getEmptyFoodStatsWithFullDate_getDatabaseErrorResponse() throws YdbClientException {
//        //arrange
//        Database database = mock(Database.class);
//        LocalDate date = LocalDate.of(2022, Month.NOVEMBER, 15);
//        when(database.findFoodRecordsByDate(eq(date))).thenReturn(List.of());
//        SkillService skill = new SkillService(database, null);
//        Event event = new Event()
//                .request(new Request().command("сколько я съела 15 ноября 2022 года"));
//        //act
//        Response response = skill.getResponse(event);
//        //assert
//        verify(database).findFoodRecordsByDate(date);
//        assertEquals(
//                "В этот день вы не вели дневник",
//                response.response().text()
//        );
//    }
//
//    @Test
//    public void getFoodStatsWithDayMonth_getSuccessfulResponse() throws YdbClientException {
//        //arrange
//        Database database = mock(Database.class);
//        LocalDate date = LocalDate.of(LocalDate.now().getYear(), Month.NOVEMBER, 15);
//        List<FoodRecord> foodRecords = List.of(new FoodRecord(
//                UUID.randomUUID().toString(),
//                "каша",
//                Instant.now(),
//                100f,
//                100f,
//                0f,
//                0f,
//                1f
//        ));
//        LocalDate actualDate = date.isAfter(LocalDate.now()) ? date.minusYears(1) : date;
//        when(database.findFoodRecordsByDate(eq(actualDate))).thenReturn(foodRecords);
//        SkillService skill = new SkillService(database, null);
//        Event event = new Event()
//                .request(new Request().command("сколько я съела 15 ноября"));
//        //act
//        Response response = skill.getResponse(event);
//        //assert
//        verify(database).findFoodRecordsByDate(actualDate);
//        assertEquals(
//                "Вы съели 100 калорий. 0 % белка, 0 % жиров, 100 % углеводов. Вы ели: каша",
//                response.response().text()
//        );
//    }
//
//    @Test
//    public void getFoodStatsWithFullDate_getSuccessfulResponse() throws YdbClientException {
//        //arrange
//        Database database = mock(Database.class);
//        LocalDate date = LocalDate.of(2022, Month.NOVEMBER, 15);
//        List<FoodRecord> foodRecords = List.of(new FoodRecord(
//                UUID.randomUUID().toString(),
//                "каша",
//                Instant.now(),
//                100f,
//                100f,
//                0f,
//                0f,
//                1f
//        ));
//        when(database.findFoodRecordsByDate(eq(date))).thenReturn(foodRecords);
//        SkillService skill = new SkillService(database, null);
//        Event event = new Event()
//                .request(new Request().command("сколько я съела 15 ноября 2022 года"));
//        //act
//        Response response = skill.getResponse(event);
//        //assert
//        verify(database).findFoodRecordsByDate(date);
//        assertEquals(
//                "Вы съели 100 калорий. 0 % белка, 0 % жиров, 100 % углеводов. Вы ели: каша",
//                response.response().text()
//        );
//    }
//
//    @Test
//    public void getFoodStatsWithDateMonthWhenYdbClientException_getYdbClientExceptionResponse() throws YdbClientException {
//        //arrange
//        Database database = mock(Database.class);
//        LocalDate date = LocalDate.of(LocalDate.now().getYear(), Month.NOVEMBER, 15);
//        LocalDate actualDate = date.isAfter(LocalDate.now()) ? date.minusYears(1) : date;
//        when(database.findFoodRecordsByDate(eq(actualDate))).thenThrow(YdbClientException.class);
//        String expectedMessage = "База данных сейчас недоступна. Повторите попытку позже";
//        SkillService skill = new SkillService(database, null);
//        Event event = new Event()
//                .request(new Request().command("сколько я съела 15 ноября"));
//        //act
//        Response response = skill.getResponse(event);
//        //assert
//        assertEquals(
//                expectedMessage,
//                response.response().text()
//        );
//    }
//
//    @Test
//    public void getFoodStatsToday_getSuccessfulResponse() throws YdbClientException {
//        //arrange
//        Database database = mock(Database.class);
//        LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
//        List<FoodRecord> foodRecords = List.of(new FoodRecord(
//                UUID.randomUUID().toString(),
//                "каша",
//                Instant.now(),
//                100f,
//                100f,
//                0f,
//                0f,
//                1f
//        ));
//        when(database.findFoodRecordsByDate(eq(date))).thenReturn(foodRecords);
//        SkillService skill = new SkillService(database, null);
//        Event event = new Event()
//                .request(new Request().command("сколько я съела сегодня"));
//        //act
//        Response response = skill.getResponse(event);
//        //assert
//        verify(database).findFoodRecordsByDate(date);
//        assertEquals(
//                "Вы съели 100 калорий. 0 % белка, 0 % жиров, 100 % углеводов. Вы ели: каша",
//                response.response().text()
//        );
//    }
//
//    @Test
//    public void getFoodStatsYesterday_getSuccessfulResponse() throws YdbClientException {
//        //arrange
//        Database database = mock(Database.class);
//        LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().minusDays(1).getDayOfMonth());
//        List<FoodRecord> foodRecords = List.of(new FoodRecord(
//                UUID.randomUUID().toString(),
//                "каша",
//                Instant.now(),
//                100f,
//                100f,
//                0f,
//                0f,
//                1f
//        ));
//        when(database.findFoodRecordsByDate(eq(date))).thenReturn(foodRecords);
//        SkillService skill = new SkillService(database, null);
//        Event event = new Event()
//                .request(new Request().command("сколько я съела вчера"));
//        //act
//        Response response = skill.getResponse(event);
//        //assert
//        verify(database).findFoodRecordsByDate(date);
//        assertEquals(
//                "Вы съели 100 калорий. 0 % белка, 0 % жиров, 100 % углеводов. Вы ели: каша",
//                response.response().text()
//        );
//    }
//
//    @Test
//    public void getFoodStatsTheDayBeforeYesterday_getSuccessfulResponse() throws YdbClientException {
//        //arrange
//        Database database = mock(Database.class);
//        LocalDate date = LocalDate.now().minusDays(2);
//        List<FoodRecord> foodRecords = List.of(new FoodRecord(
//                UUID.randomUUID().toString(),
//                "каша",
//                Instant.now(),
//                100f,
//                100f,
//                0f,
//                0f,
//                1f
//        ));
//        when(database.findFoodRecordsByDate(eq(date))).thenReturn(foodRecords);
//        SkillService skill = new SkillService(database, null);
//        Event event = new Event()
//                .request(new Request().command("сколько я съела позавчера"));
//        //act
//        Response response = skill.getResponse(event);
//        //assert
//        verify(database).findFoodRecordsByDate(date);
//        assertEquals(
//                "Вы съели 100 калорий. 0 % белка, 0 % жиров, 100 % углеводов. Вы ели: каша",
//                response.response().text()
//        );
//    }
//
//    @Test
//    public void getEmptyFoodStatsToday_getDatabaseErrorResponse() throws YdbClientException {
//        //arrange
//        Database database = mock(Database.class);
//        LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
//        when(database.findFoodRecordsByDate(eq(date))).thenReturn(List.of());
//        SkillService skill = new SkillService(database, null);
//        Event event = new Event()
//                .request(new Request().command("сколько я съела сегодня"));
//        //act
//        Response response = skill.getResponse(event);
//        //assert
//        verify(database).findFoodRecordsByDate(date);
//        assertEquals(
//                "В этот день вы не вели дневник",
//                response.response().text()
//        );
//    }
//
//    @Test
//    public void getFoodStatsTodayWhenYdbClientException_getYdbClientExceptionResponse() throws YdbClientException {
//        //arrange
//        Database database = mock(Database.class);
//        LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
//        when(database.findFoodRecordsByDate(eq(date))).thenThrow(new YdbClientException(null));
//        String expectedMessage = "База данных сейчас недоступна. Повторите попытку позже";
//        SkillService skill = new SkillService(database, null);
//        Event event = new Event()
//                .request(new Request().command("сколько я съела сегодня"));
//        //act
//        Response response = skill.getResponse(event);
//        //assert
//        assertEquals(
//                expectedMessage,
//                response.response().text()
//        );
//    }
}
