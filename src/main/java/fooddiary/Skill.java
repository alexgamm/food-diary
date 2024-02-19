package fooddiary;

import fooddiary.command.AddFoodCommand;
import fooddiary.command.Command;
import fooddiary.command.DeleteLastFoodCommand;
import fooddiary.command.GetFoodStatsCommand;
import fooddiary.database.Database;
import fooddiary.fatsecret.FoodSearch;
import fooddiary.utils.Responses;
import yacloud.Event;
import yacloud.NluEntity;
import yacloud.Response;
import yacloud.TextResponse;

import java.util.List;
import java.util.Map;


public class Skill {

    private final List<Command> commands;

    public Skill(Database database, FoodSearch foodSearch) {
        this.commands = List.of(
                new AddFoodCommand(foodSearch, database),
                new DeleteLastFoodCommand(database),
                new GetFoodStatsCommand(database)
        );
    }

    public Response getResponse(Event event) {
        PersonRequest personRequest = getPersonRequest(event);
        for (Command command : commands) {
            if (command.isRelevant(personRequest.getRequest())) {
                return createResponse(event, command.getResponse(personRequest));
            }
        }
        return createResponse(event, Responses.invalidCommand());
    }

    private Response createResponse(Event event, String responseText) {
        return new Response()
                .session(event.session())
                .version(event.version())
                .response(new TextResponse().text(responseText).end_session(true));
    }

    private static PersonRequest getPersonRequest(Event event) {
        String request = event.request().command();
        List<String> tokens = event.request().nlu().tokens();
        String firstName = null;
        if (tokens.get(0).equals("для")) {
            NluEntity fullNameEntity = event.request().nlu().entities().stream()
                    .filter(nluEntity -> nluEntity.type().equals("YANDEX.FIO"))
                    .findFirst()
                    .orElse(null);
            if (fullNameEntity != null) {
                //noinspection unchecked
                firstName = ((Map<String, String>) fullNameEntity.value()).get("first_name");
                request = String.join(" ", tokens.subList(fullNameEntity.tokens().end(), tokens.size()));
            }
        }
        return new PersonRequest(event.session().user().user_id(), firstName, request);
    }
}



