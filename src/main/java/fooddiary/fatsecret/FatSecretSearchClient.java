package fooddiary.fatsecret;

import fooddiary.model.Food;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
@Component
public class FatSecretSearchClient {
    private static final String SEARCH_URL = "https://www.fatsecret.ru/%D0%BA%D0%B0%D0%BB%D0%BE%D1%80%D0%B8%D0%B8-%D0%BF%D0%B8%D1%82%D0%B0%D0%BD%D0%B8%D0%B5/search?";
    private final WebClient webClient;

    public FatSecretSearchClient(@Qualifier("noSslWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public List<Food> search(@NotNull String query) {
        String html = webClient.get()
                .uri(SEARCH_URL, uriBuilder -> uriBuilder.queryParam("q", query).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return Parser.parseResponse(html);
    }
}
