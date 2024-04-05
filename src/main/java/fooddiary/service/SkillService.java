package fooddiary.service;

import fooddiary.command.AddFoodCommand;
import fooddiary.command.Command;
import fooddiary.command.DeleteLastFoodCommand;
import fooddiary.command.GetFoodStatsCommand;
import fooddiary.database.Database;
import fooddiary.model.PersonRequest;
import fooddiary.utils.Responses;
import fooddiary.yacloud.Event;
import fooddiary.yacloud.NluEntity;
import fooddiary.yacloud.Response;
import fooddiary.yacloud.TextResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SkillService {

    private final List<Command> commands;

    public SkillService(Database database, FoodSearchService foodSearch) {
        this.commands = List.of(
                new AddFoodCommand(foodSearch, database),
                new DeleteLastFoodCommand(database),
                new GetFoodStatsCommand(database)
        );
    }

    public Response getResponse(Event event) {
        PersonRequest personRequest = getPersonRequest(event);
        log.info("Created person request {} from event {}", personRequest, event);
        for (Command command : commands) {
            if (command.isRelevant(personRequest.request())) {
                return createResponse(event, command.getResponse(personRequest));
            }
        }
        return createResponse(event, Responses.invalidCommand());
    }

    private Response createResponse(Event event, String responseText) {
        return new Response()
                .setSession(event.getSession())
                .setVersion(event.getVersion())
                .setResponse(new TextResponse().setText(responseText).setEnd_session(true));
    }

    private static PersonRequest getPersonRequest(Event event) {
        String request = event.getRequest().getCommand();
        List<String> tokens = event.getRequest().getNlu().getTokens();
        String firstName = null;
        if (tokens.stream().findFirst().map(token -> token.equals("для")).orElse(false)) {
            NluEntity fullNameEntity = event.getRequest().getNlu().getEntities().stream()
                    .filter(nluEntity -> nluEntity.getType().equals("YANDEX.FIO"))
                    .findFirst()
                    .orElse(null);
            if (fullNameEntity != null) {
                //noinspection unchecked
                firstName = ((Map<String, String>) fullNameEntity.getValue()).get("first_name");
                request = String.join(" ", tokens.subList(fullNameEntity.getTokens().getEnd(), tokens.size()));
            }
        }
        return new PersonRequest(event.getSession().getUser().getUser_id(), firstName, request);
    }
}



