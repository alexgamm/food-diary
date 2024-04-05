package fooddiary.utils.date;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
@RequiredArgsConstructor
public class FullDate implements DateVariant {
    public static final DateTimeFormatter FULL_DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"));
    private final Clock clock;

    @Override
    public LocalDate parse(String text) {
        return LocalDate.parse(text, FULL_DATE_FORMATTER);
    }

    @Override
    public String getPattern() {
        return "(\\d+ [А-я]+ \\d+)";
    }
}
