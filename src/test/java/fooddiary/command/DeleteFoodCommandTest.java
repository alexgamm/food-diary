package fooddiary.command;

import com.github.vdybysov.ydb.exception.YdbClientException;
import fooddiary.database.Database;
import fooddiary.database.FoodRecord;
import fooddiary.model.PersonRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DeleteFoodCommandTest extends CommandTest {
    @BeforeEach
    public void setup() {
        database = mock(Database.class);
        command = new DeleteLastFoodCommand(database);
    }

    @Test
    public void deleteExistingLastFoodRecord_getSuccessfulResponse() throws YdbClientException {
        //arrange
        PersonRequest personRequest = personRequest("удалить");
        when(database.deleteLastFood(personRequest.getPersonId())).thenReturn(new FoodRecord(
                UUID.randomUUID().toString(),
                personRequest.getPersonId(),
                "каша",
                Instant.now(),
                100f,
                100f,
                0f,
                0f,
                1f
        ));
        //act
        String response = command.getResponse(personRequest);
        //assert
        verify(database).deleteLastFood(personRequest.getPersonId());
        assertEquals("Успешно удалила каша из дневника питания", response);
    }

    @Test
    public void deleteMissingLastFoodRecord_getUnsuccessfulResponse() throws YdbClientException {
        //arrange
        PersonRequest personRequest = personRequest("удалить");
        when(database.deleteLastFood(personRequest.getPersonId())).thenReturn(null);
        //act
        String response = command.getResponse(personRequest);
        //assert
        verify(database).deleteLastFood(personRequest.getPersonId());
        assertEquals("К сожалению, найти последнюю запись не удалось", response);
    }
}
