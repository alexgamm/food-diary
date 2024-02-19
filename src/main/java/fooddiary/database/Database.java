package fooddiary.database;

import com.github.vdybysov.ydb.client.YdbClientBuilder;
import com.github.vdybysov.ydb.exception.YdbClientException;
import com.github.vdybysov.ydb.typed.TypedYdbClient;

import java.time.LocalDate;
import java.util.List;

public class Database {

    private final TypedYdbClient client;

    public Database(String connectionString, String iamToken) {
        this.client = new TypedYdbClient(YdbClientBuilder.forConnectionString(connectionString, iamToken));
    }

    public void addFood(FoodRecord record) throws YdbClientException {
        String query = """
                DECLARE $id as String;
                DECLARE $personId as String;
                DECLARE $carbohydrate as Float;
                DECLARE $date as Timestamp;
                DECLARE $fat as Float;
                DECLARE $name as Utf8;
                DECLARE $grams as Float;
                DECLARE $kcal as Float;
                DECLARE $protein as Float;
                UPSERT INTO my_foods (id, personId, carbohydrate, date, fat, name, grams, kcal, protein)
                VALUES ($id, $personId, $carbohydrate, $date, $fat, $name, $grams, $kcal, $protein)
                """.stripIndent();
        client.executeQuery(query, record);
    }

    public List<FoodRecord> findFoodRecordsByDate(LocalDate date, String personId) throws YdbClientException {
        String query = """
                DECLARE $date AS Date;
                DECLARE $personId AS String;
                SELECT * FROM my_foods WHERE cast(date as Date) = $date and personId = $personId;
                """.stripIndent();
        return client.executeQuery(
                query,
                new SelectByDateParams(date, personId),
                FoodRecord.class
        ).stream().toList();
    }

    public boolean deleteLastFood(String personId) throws YdbClientException {
        String query = "SELECT * FROM my_foods WHERE personId is null ORDER BY `date` DESC LIMIT 1;";
        FoodRecord foodRecord = client.executeQuery(
                query,
                personId,
                FoodRecord.class
        ).stream().findFirst().orElse(null);
        if (foodRecord == null) {
            return false;
        }
        client.executeQuery("""
                        DECLARE $id as String;
                        DELETE FROM my_foods WHERE id = $id
                        """.stripIndent(),
                foodRecord
        );
        return true;
    }
}
