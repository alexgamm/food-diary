package fooddiary;

import com.github.vdybysov.ydb.exception.YdbClientException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fooddiary.database.Database;
import fooddiary.database.FoodRecord;
import fooddiary.fatsecret.FoodSearch;
import yacloud.Event;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws SQLException, YdbClientException {
//        long start = System.currentTimeMillis();
//        Database database = new Database(System.getenv("DB_CONNECTION_STRING"), System.getenv("IAM_TOKEN"));
//        FoodRecord newFoodRecord = new FoodRecord(UUID.randomUUID().toString(), "Apple", Instant.now(), 100.0f, 52.0f, 0.2f, 0.3f, 13.8f);
//      //  databaseApi.addFood(newFoodRecord);
//        database.deleteLastFood();
//        List<FoodRecord> foodRecordsByDate = database.findFoodRecordsByDate(LocalDate.now());
//        System.out.println(foodRecordsByDate);
//        System.out.println(System.currentTimeMillis() - start);
        Skill skill = new Skill(new Database(System.getenv("DB_CONNECTION_STRING"), System.getenv("IAM_TOKEN")), new FoodSearch());
        Event event = new Gson().fromJson("{\n" +
                "  \"meta\": {\n" +
                "    \"locale\": \"ru-RU\",\n" +
                "    \"timezone\": \"UTC\",\n" +
                "    \"client_id\": \"ru.yandex.searchplugin/7.16 (none none; android 4.4.2)\",\n" +
                "    \"interfaces\": {\n" +
                "      \"screen\": {},\n" +
                "      \"payments\": {},\n" +
                "      \"account_linking\": {}\n" +
                "    }\n" +
                "  },\n" +
                "  \"session\": {\n" +
                "    \"message_id\": 0,\n" +
                "    \"session_id\": \"4f1e5782-2eea-482d-85cd-1b99228eb722\",\n" +
                "    \"skill_id\": \"40d92197-6415-4d71-a2de-dfcf5a70ccdf\",\n" +
                "    \"user\": {\n" +
                "      \"user_id\": \"329BAFE34A1F4BC94A6A31E5C9BDF52A1FC52E356A58EB021B9DBBB6730688CD\"\n" +
                "    },\n" +
                "    \"application\": {\n" +
                "      \"application_id\": \"8FE2F246A5BBB97BEF81B32995AE880D31829E0C32B701E8FF5F27E1BCFB139C\"\n" +
                "    },\n" +
                "    \"new\": true,\n" +
                "    \"user_id\": \"8FE2F246A5BBB97BEF81B32995AE880D31829E0C32B701E8FF5F27E1BCFB139C\"\n" +
                "  },\n" +
                "  \"request\": {\n" +
                "    \"command\": \"для влада добавить пиво корона 330 грам\",\n" +
                "    \"original_utterance\": \"для влада добавить пиво корона 330 грам\",\n" +
                "    \"nlu\": {\n" +
                "      \"tokens\": [\n" +
                "        \"для\",\n" +
                "        \"влада\",\n" +
                "        \"добавить\",\n" +
                "        \"пиво\",\n" +
                "        \"корона\",\n" +
                "        \"330\",\n" +
                "        \"грам\"\n" +
                "      ],\n" +
                "      \"entities\": [\n" +
                "        {\n" +
                "          \"type\": \"YANDEX.FIO\",\n" +
                "          \"tokens\": {\n" +
                "            \"start\": 1,\n" +
                "            \"end\": 2\n" +
                "          },\n" +
                "          \"value\": {\n" +
                "            \"first_name\": \"влад\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"YANDEX.NUMBER\",\n" +
                "          \"tokens\": {\n" +
                "            \"start\": 5,\n" +
                "            \"end\": 6\n" +
                "          },\n" +
                "          \"value\": 330\n" +
                "        }\n" +
                "      ],\n" +
                "      \"intents\": {}\n" +
                "    },\n" +
                "    \"markup\": {\n" +
                "      \"dangerous_context\": false\n" +
                "    },\n" +
                "    \"type\": \"SimpleUtterance\"\n" +
                "  },\n" +
                "  \"version\": \"1.0\"\n" +
                "}", Event.class);
        skill.getResponse(event);
    }
}