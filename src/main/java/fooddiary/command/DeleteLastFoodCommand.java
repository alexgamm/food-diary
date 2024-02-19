package fooddiary.command;

import com.github.vdybysov.ydb.exception.YdbClientException;
import fooddiary.PersonRequest;
import fooddiary.database.Database;
import fooddiary.utils.Responses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteLastFoodCommand implements Command {
    private final Database database;

    @Override
    public String getResponse(PersonRequest personRequest) {
        try {
            database.deleteLastFood(personRequest.getPersonId());
            return Responses.successfulFoodDelete();
        } catch (YdbClientException e) {
            return Responses.dbError();
        }
    }

    @Override
    public boolean isRelevant(String request) {
        return request.startsWith("удалить");
    }
}
