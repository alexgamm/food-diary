package fooddiary.command;

import com.github.vdybysov.ydb.exception.YdbClientException;
import fooddiary.model.PersonRequest;
import fooddiary.database.Database;
import fooddiary.database.FoodRecord;
import fooddiary.utils.DateParser;
import fooddiary.utils.EatenFoodStats;
import fooddiary.utils.Responses;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class GetFoodStatsCommand implements Command {

    private final Database database;

    @Override
    public String getResponse(PersonRequest personRequest) {
        DateParser dateParser = new DateParser(Clock.systemUTC());
        LocalDate date = dateParser.parse(personRequest.request());
        List<FoodRecord> foodRecords;
        try {
            foodRecords = database.findFoodRecordsByDate(date, personRequest.getPersonId());
        } catch (YdbClientException e) {
            return Responses.dbError();
        }
        if (foodRecords.isEmpty()) {
            return Responses.foodRecordsNotFound();
        }
        EatenFoodStats foodStats = EatenFoodStats.of(foodRecords);
        return Responses.eatenFoodStats(foodStats);
    }

    @Override
    public boolean isRelevant(String request) {
        return request.matches("^(сколько|что) я (съ)?ел(а)?(.+)$");
    }
}
