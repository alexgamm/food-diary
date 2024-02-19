package fooddiary.utils.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PartialDate implements DateVariant {

    public static final DateTimeFormatter DATE_MONTH_FORMATTER = DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"));

    @Override
    public LocalDate parse(String text) {
        LocalDate date = MonthDay.parse(text, DATE_MONTH_FORMATTER).atYear(LocalDate.now().getYear());
        return date.isAfter(LocalDate.now()) ? date.minusYears(1) : date;
    }

    @Override
    public String getPattern() {
        return "(\\d+ [А-я]+)";
    }
}
