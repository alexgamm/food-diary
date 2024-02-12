package fooddiary.fatsecret;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Parser {
    public static List<Food> parseResponse(String html) {
        Document doc = Jsoup.parse(html);
        Elements mainClasses = doc.select("td.borderBottom");
        if (mainClasses.isEmpty()) {
            return Collections.emptyList();
        }
        List<Food> foods = new ArrayList<>();
        for (Element mainClass : mainClasses) {
            String foodName = mainClass.select("> a").stream()
                    .map(Element::text)
                    .collect(Collectors.joining(" "));
            String foodStatsText = mainClass.select("td.borderBottom div").stream()
                    .filter(div -> div.text().startsWith("в 100"))
                    .findFirst()
                    .map(Element::text)
                    .orElse(null);
            if (foodName.isEmpty() || foodStatsText == null) continue;
            Optional<Float> kcal = parseStat("Калории", foodStatsText);
            if (kcal.isEmpty()) continue;
            foods.add(new Food(
                    foodName,
                    kcal.get(),
                    parseStat("Белк", foodStatsText).orElse(0f),
                    parseStat("Жир", foodStatsText).orElse(0f),
                    parseStat("Углев", foodStatsText).orElse(0f)
            ));
        }
        return foods;
    }

    private static Optional<Float> parseStat(String statName, String fullStatsText) {
        Pattern pattern = Pattern.compile(String.format("(%s: )(\\d*[,.]*\\d*)", statName));
        Matcher matcher = pattern.matcher(fullStatsText);
        if (matcher.find()) {
            return Optional.of(Float.valueOf(matcher.group(2).replace(",", ".")));
        } else {
            return Optional.empty();
        }
    }
}
