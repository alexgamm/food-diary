package fooddiary.command;

import fooddiary.database.Database;
import fooddiary.model.PersonRequest;
import fooddiary.service.FoodSearchService;
import lombok.RequiredArgsConstructor;

import static org.mockito.Mockito.mock;

@RequiredArgsConstructor
public abstract class CommandTest {
    protected Database database = mock(Database.class);
    protected FoodSearchService foodSearch = mock(FoodSearchService.class);
    protected Command command;

    protected static PersonRequest personRequest(String request) {
        return new PersonRequest("testUserId", null, request);
    }
}
