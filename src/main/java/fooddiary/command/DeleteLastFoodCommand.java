package fooddiary.command;

import com.github.vdybysov.ydb.exception.YdbClientException;
import fooddiary.model.PersonRequest;
import fooddiary.database.Database;
import fooddiary.database.FoodRecord;
import fooddiary.utils.Responses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteLastFoodCommand implements Command {
    private final Database database;

    @Override
    public String getResponse(PersonRequest personRequest) {
        FoodRecord foodRecord;
        try {
            foodRecord = database.deleteLastFood(personRequest.getPersonId());
        } catch (YdbClientException e) {
            return Responses.dbError();
        }
        if (foodRecord == null) {
            return Responses.unsuccessfulFoodDelete();
        }
        return Responses.successfulFoodDelete(foodRecord.name());
    }

    @Override
    public boolean isRelevant(String request) {
        return request.startsWith("удалить");
    }
}
