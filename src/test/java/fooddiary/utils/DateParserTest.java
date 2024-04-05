package fooddiary.utils;

import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateParserTest {
    private final DateParser dateParser = new DateParser(Clock.fixed(
            LocalDateTime.of(2024, 2, 24, 19, 40).toInstant(ZoneOffset.UTC),
            ZoneId.of("UTC")
    ));

    @Test
    public void parseFullDate() {
        //arrange
        String command = "сколько я съела 15 ноября 2022 года";
        //act
        LocalDate date = dateParser.parse(command);
        //assert
        assertEquals(LocalDate.of(2022, Month.NOVEMBER, 15), date);
    }

    @Test
    public void parsePartialDateLastYear() {
        //arrange
        String command = "сколько я съела 15 ноября";
        //act
        LocalDate parsedDate = dateParser.parse(command);
        //assert
        assertEquals(LocalDate.of(2023, Month.NOVEMBER, 15), parsedDate);
    }

    @Test
    public void parsePartialDateThisYear() {
        //arrange
        String command = "сколько я съела 1 февраля";
        //act
        LocalDate parsedDate = dateParser.parse(command);
        //assert
        assertEquals(LocalDate.of(2024, Month.FEBRUARY, 1), parsedDate);
    }

    @Test
    public void parseYesterday() {
        //arrange
        String command = "сколько я съела вчера";
        //act
        LocalDate parsedDate = dateParser.parse(command);
        //assert
        assertEquals(LocalDate.of(2024, Month.FEBRUARY, 23), parsedDate);
    }

    @Test
    public void parseTheDayBeforeYesterday() {
        //arrange
        String command = "сколько я съела позавчера";
        //act
        LocalDate parsedDate = dateParser.parse(command);
        //assert
        assertEquals(LocalDate.of(2024, Month.FEBRUARY, 22), parsedDate);
    }

}
