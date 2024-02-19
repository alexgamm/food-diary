package fooddiary.utils;

import fooddiary.utils.date.DateVariant;
import fooddiary.utils.date.FullDate;
import fooddiary.utils.date.PartialDate;
import fooddiary.utils.date.SubtractDays;

import java.time.LocalDate;
import java.util.List;

public class DateParser {
    private static final List<DateVariant> DATE_VARIANTS = List.of(
            new FullDate(),
            new PartialDate(),
            SubtractDays.of("(позавчера)", 2),
            SubtractDays.of("(вчера)", 1)
    );

    public static LocalDate parse(String text) {
        for (DateVariant dateVariant : DATE_VARIANTS) {
            if (dateVariant.isRelevant(text)) {
                return dateVariant.findAndParse(text);
            }
        }
        return LocalDate.now();
    }


}
