package fatsecret;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Parser {
    public List<Food> parseResponse(String html) {
        List<Food> foods = new ArrayList<>();
        Document doc = Jsoup.parse(html);

        Elements mainClasses = doc.select("td.borderBottom");
        Map<String, String> foodNamesWithUnparsedStats = new HashMap<>();
        if (!mainClasses.isEmpty()) {
            for (Element mainClass : mainClasses) {
                String foodName = mainClass.select("> a").stream().map(Element::text).collect(Collectors.joining(" "));
                Element foodStats = mainClass.select("td.borderBottom div").stream().filter(div -> div.text().startsWith("в 100г -")).findFirst().orElse(null);
                if (!foodName.isEmpty() && foodStats != null) {
                    foodNamesWithUnparsedStats.put(foodName, foodStats.text());
                }
            }
        }
        foodNamesWithUnparsedStats.forEach((key, value) -> {
            Map<String, Float> parsedFoodStats = getFoodStats(value);
            foods.add(new Food(
                    key,
                    parsedFoodStats.get("kcal"),
                    parsedFoodStats.get("protein"),
                    parsedFoodStats.get("fat"),
                    parsedFoodStats.get("carbs")
            ));
        });
        return foods;
    }

    private Map<String, Float> getFoodStats(String value) {
        Map<String, Float> foodStats = new HashMap<>();
        String kcalPattern = "(Калории: )(\\d*[,.]*\\d*)";
        Pattern kp = Pattern.compile(kcalPattern);
        Matcher km = kp.matcher(value);
        if (km.find()) {
            foodStats.put("kcal", Float.valueOf(km.group(2).replace(",", ".")));
        } else {
            return Collections.emptyMap();
        }

        String fatPattern = "(Жир: )(\\d*[,.]*\\d*)";
        Pattern fp = Pattern.compile(fatPattern);
        Matcher fm = fp.matcher(value);
        if (fm.find()) {
            foodStats.put("fat", Float.valueOf(fm.group(2).replace(",", ".")));
        } else {
            foodStats.put("fat", 0f);
        }

        String carbsPattern = "(Углев: )(\\d*[,.]*\\d*)";
        Pattern cp = Pattern.compile(carbsPattern);
        Matcher cm = cp.matcher(value);
        if (cm.find()) {
            foodStats.put("carbs", Float.valueOf(cm.group(2).replace(",", ".")));
        } else {
            foodStats.put("carbs", 0f);
        }

        String ProteinPattern = "(Белк: )(\\d*[,.]*\\d*)";
        Pattern pp = Pattern.compile(ProteinPattern);
        Matcher pm = pp.matcher(value);
        if (pm.find()) {
            foodStats.put("protein", Float.valueOf(pm.group(2).replace(",", ".")));
        } else {
            foodStats.put("protein", 0f);
        }
        return foodStats;
    }
}
