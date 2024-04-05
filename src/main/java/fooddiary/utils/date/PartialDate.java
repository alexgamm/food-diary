package fooddiary.utils.date;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
@RequiredArgsConstructor
public class PartialDate implements DateVariant {

    public static final DateTimeFormatter DATE_MONTH_FORMATTER = DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"));
    private final Clock clock;

    @Override
    public LocalDate parse(String text) {
        LocalDate today = LocalDate.now(getClock());
        LocalDate date = MonthDay.parse(text, DATE_MONTH_FORMATTER).atYear(today.getYear());
        return date.isAfter(today) ? date.minusYears(1) : date;
    }

    @Override
    public String getPattern() {
        return "(\\d+ [А-я]+)";
    }
}
