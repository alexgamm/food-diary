package fooddiary;

import fatsecret.Request;
import fooddiary.database.DatabaseApi;
import fooddiary.database.FoodRecord;
import fooddiary.database.exception.DatabaseApiException;
import fooddiary.google.api.TranslationRequest;
import fooddiary.usda.api.UsdaApiClient;
import yacloud.Event;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException, ExecutionException, InterruptedException, TimeoutException, DatabaseApiException {
       // ApiHttpRequests apiHttpRequests = new ApiHttpRequests();
//        FoodRecord groceryFood = apiHttpRequests.findFood("Sausage", 100, 170);
//        apiHttpRequests.findBasicFood("carrot", 100);
//        apiHttpRequests.findHomeFood("dsfsd", 100);
      //  DatabaseApi database = new DatabaseApi();
//        database.launchConnect();
        //      database.upsertSimple(groceryFood);
//        long start = System.currentTimeMillis();
//        new UsdaApiClient(System.getenv("USDA_API_KEY")).search("soup", null);
//        System.out.println("1 " + (System.currentTimeMillis()-start));
//        TranslationRequest translationRequest = new TranslationRequest();
//        translationRequest.translate("Пряник asd");
//        System.out.println("2 " + (System.currentTimeMillis()-start));
//        List<FoodRecord> foods = database.findFoodRecordsByDate(LocalDate.of(2024, 1, 24));
//        for (FoodRecord foodRecord : foods) {
//            System.out.println(foodRecord.getFoodName() + " " + foodRecord.getDate() + " " + foodRecord.getKcal() + ". ");
//        }
//        System.out.println("3 " + (System.currentTimeMillis()-start));
        // method calculating calories and PFC in percents per day + gives food names
//        float overallFat = 0f;
//        float overallProtein = 0f;
//        float overallCarbs = 0f;
//        float overallKcal = 0f;
//        for (FoodRecord foodRecord : foods) {
//            overallFat += foodRecord.getTotalFat();
//            overallProtein += foodRecord.getProtein();
//            overallCarbs += foodRecord.getCarbohydrate();
//            overallKcal += foodRecord.getKcal();
//        }
//        TranslationRequest translationRequest = new TranslationRequest();
//        translationRequest.translate("Пряник asd");

        // what is the most calorific food today
      //  new Handler().apply(new Event().request(new Request().command("добавить морковь 100 грамм")));
        Request request = new Request();
        System.out.println(request.search("гематоген"));
    }
}