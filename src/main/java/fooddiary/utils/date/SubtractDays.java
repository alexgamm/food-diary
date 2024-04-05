package fooddiary.utils.date;

import java.time.Clock;
import java.time.LocalDate;

public interface SubtractDays extends DateVariant {
    int daysToSubtract();

    default LocalDate parse(String text) {
        return LocalDate.now(getClock()).minusDays(daysToSubtract());
    }

    static SubtractDays of(Clock clock, String pattern, int daysToSubtract) {
        return new SubtractDays() {
            @Override
            public int daysToSubtract() {
                return daysToSubtract;
            }

            @Override
            public String getPattern() {
                return pattern;
            }

            @Override
            public Clock getClock() {
                return clock;
            }
        };
    }

}
