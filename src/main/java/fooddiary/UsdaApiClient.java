package fooddiary;

import fooddiary.model.api.ApiSearchResponse;
import fooddiary.model.api.DataType;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class UsdaApiClient {
    private final String apiKey;
    private final static String API_SEARCH_URL = "https://api.nal.usda.gov/fdc/v1/foods/search?";


    public ApiSearchResponse search(@NotNull String query, @Nullable DataType dataType) {
        GetRequest getRequest = Unirest.get(API_SEARCH_URL)
                .queryString("api_key", apiKey)
                .queryString("query", query);
        if (dataType != null) {
            getRequest.queryString("dataType", dataType);
        }
      return getRequest.asObject(ApiSearchResponse.class).getBody();
    }
}
