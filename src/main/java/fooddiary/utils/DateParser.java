package fooddiary.utils;

import fooddiary.utils.date.DateVariant;
import fooddiary.utils.date.FullDate;
import fooddiary.utils.date.PartialDate;
import fooddiary.utils.date.SubtractDays;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class DateParser {

    private final List<DateVariant> dateVariants;

    public DateParser(Clock clock) {
        this.dateVariants = List.of(
                new FullDate(clock),
                new PartialDate(clock),
                SubtractDays.of(clock, "(позавчера)", 2),
                SubtractDays.of(clock, "(вчера)", 1)
        );
    }

    public LocalDate parse(String text) {
        for (DateVariant dateVariant : dateVariants) {
            if (dateVariant.isRelevant(text)) {
                return dateVariant.findAndParse(text);
            }
        }
        return LocalDate.now();
    }

}
