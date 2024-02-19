package fooddiary.utils.date;

import java.time.LocalDate;

public interface SubtractDays extends DateVariant {
    int daysToSubtract();

    default LocalDate parse(String text) {
        return LocalDate.now().minusDays(daysToSubtract());
    }

    static SubtractDays of(String pattern, int daysToSubtract) {
        return new SubtractDays() {
            @Override
            public int daysToSubtract() {
                return daysToSubtract;
            }

            @Override
            public String getPattern() {
                return pattern;
            }
        };
    }

}
