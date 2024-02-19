package fooddiary;

import fooddiary.database.Database;
import fooddiary.fatsecret.FoodSearch;
import kong.unirest.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yacloud.Event;
import yacloud.Response;
import yandex.cloud.sdk.functions.Context;
import yandex.cloud.sdk.functions.YcFunction;
@Slf4j
public class Handler implements YcFunction<Event, Response> {

    @Override
    public Response handle(Event event, Context context) {
       log.info("{} - {}", event.request().command(), event.request().nlu());
        JSONObject tokenObject = new JSONObject(context.getTokenJson());
        Skill skill = new Skill(
                new Database(System.getenv("DB_CONNECTION_STRING"), tokenObject.getString("access_token")),
                new FoodSearch()
        );
        return skill.getResponse(event);
    }
}
