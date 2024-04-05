package fooddiary.utils.date;

import java.time.Clock;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface DateVariant {
    //20 декабря
    LocalDate parse(String text);

    String getPattern();
    Clock getClock();

    default boolean isRelevant(String text) {
        return getMatcher(text).find();
    }

    default LocalDate findAndParse(String text) {
        Matcher matcher = getMatcher(text);
        if (!matcher.find()) {
            throw new IllegalArgumentException();
        }
        return parse(matcher.group(1));
    }

    //сколько я ел 20 декабря
    default Matcher getMatcher(String text) {
        return Pattern.compile(getPattern()).matcher(text);
    }
}
