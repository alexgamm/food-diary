package fooddiary.fatsecret;

import kong.unirest.Unirest;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HttpRequest {
    private final static String SEARCH_URL = System.getenv("FAT_SECRET_SEARCH_URL");

    public List<Food> search(@NotNull String query) {
        Unirest.config().verifySsl(false);
        String html = Unirest.get(SEARCH_URL)
                .queryString("q", query)
                .asString().getBody();
        Parser parser = new Parser();
        return parser.parseResponse(html);
    }
}
