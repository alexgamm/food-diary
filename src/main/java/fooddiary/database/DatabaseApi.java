package fooddiary.database;

import fooddiary.database.exception.DatabaseApiException;
import kong.unirest.Empty;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public class DatabaseApi {
    private static final Logger log = LoggerFactory.getLogger(DatabaseApi.class); //@Slf4j
    private static final String CLOUD_URL = "https://functions.yandexcloud.net/";
    private static final String GET_FOOD_URL = CLOUD_URL + System.getenv("GET_FOOD_FUNCTION_ID");
    private static final String ADD_FOOD_URL = CLOUD_URL + System.getenv("ADD_FOOD_FUNCTION_ID");

    public void addFood(FoodRecord record) throws DatabaseApiException {
        HttpResponse<Empty> response = Unirest.post(ADD_FOOD_URL)
                .header("Content-Type", "application/json")
                .body(record)
                .asEmpty();
        if (!response.isSuccess()) {
            throw new DatabaseApiException();
        }
    }

    public List<FoodRecord> findFoodRecordsByDate(LocalDate date) throws DatabaseApiException {
        HttpResponse<FoodRecord[]> response = Unirest.get(GET_FOOD_URL)
                .queryString("date", date)
                .asObject(FoodRecord[].class);
        if (!response.isSuccess()) {
            throw new DatabaseApiException();
        }
        return List.of(response.getBody());
    }

}
