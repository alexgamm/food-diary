package fooddiary.command;

import com.github.vdybysov.ydb.exception.YdbClientException;
import fooddiary.database.FoodRecord;
import fooddiary.model.Food;
import fooddiary.model.PersonRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AddFoodCommandTest extends CommandTest {
    @BeforeAll
    public void setup() {
        command = new AddFoodCommand(foodSearch, database);
    }

    @Test
    public void addFoodWithoutFoodName_getInvalidCommandResponse() {
        //arrange
        PersonRequest personRequest = personRequest("добавить 100 грамм");
        //act
        String response = command.getResponse(personRequest);
        //assert
        assertEquals("Упс, что-то не так с твоим запросом. Скажи еще раз", response);
    }

    @Test
    public void addFoodWithoutCaloriesWithoutGramWord_getInvalidCommandResponse() {
        //arrange
        PersonRequest personRequest = personRequest("добавить жаренная картошка 100");
        //act
        String response = command.getResponse(personRequest);
        //assert
        assertEquals("Упс, что-то не так с твоим запросом. Скажи еще раз", response);
    }

    @Test
    public void addFoodWithoutGramWord_getInvalidCommandResponse() {
        //arrange
        PersonRequest personRequest = personRequest("добавить жаренная картошка 100");
        //act
        String response = command.getResponse(personRequest);
        //assert
        assertEquals("Упс, что-то не так с твоим запросом. Скажи еще раз", response);
    }

    @Test
    public void addUnknownFoodWithoutCalories_getUnknownDishResponse() {
        //arrange
        when(foodSearch.search(eq("тапок"), eq(100f))).thenReturn(Optional.empty());

        PersonRequest personRequest = personRequest("добавить тапок 100 грамм");
        //act
        String response = command.getResponse(personRequest);
        //assert
        verifyNoInteractions(database);
        assertEquals(
                "К сожалению, тапок найти не удалось",
                response
        );
    }

    @Test
    public void addUnknownFoodWithCalories_getUnknownFoodResponse() {
        //arrange
        when(foodSearch.search(eq("тапок"), eq(50f))).thenReturn(Optional.empty());
        PersonRequest personRequest = personRequest("добавить тапок 100 грамм 50 калорий");
        //act
        String response = command.getResponse(personRequest);
        //assert
        verifyNoInteractions(database);
        assertEquals(
                "К сожалению, тапок найти не удалось",
                response
        );
    }

    @Test
    public void addFoodWithCalories_saveFoodAndSuccessfulResponse() throws YdbClientException {
        //arrange
        when(foodSearch.search(eq("вафли"), eq(300f))).thenReturn(Optional.of(new Food(
                "вафли молочные реки",
                300,
                0f,
                3f,
                10f
        )));
        PersonRequest personRequest = personRequest("добавить вафли 100 грамм 300 калорий");
        //act
        String response = command.getResponse(personRequest);
        //assert
        assertEquals(
                "Успешно добавила вафли молочные реки или как вы это называете вафли в дневник питания",
                response
        );
        ArgumentCaptor<FoodRecord> addFoodCaptor = ArgumentCaptor.forClass(FoodRecord.class);
        verify(database).addFood(addFoodCaptor.capture());
        FoodRecord capturedFoodRecord = addFoodCaptor.getValue();
        assertEquals(personRequest.getPersonId(), capturedFoodRecord.personId());
        assertEquals("вафли молочные реки", capturedFoodRecord.name());
        assertNotNull(capturedFoodRecord.date());
        assertNotNull(capturedFoodRecord.id());
        assertEquals(100f, capturedFoodRecord.grams());
        assertEquals(300f, capturedFoodRecord.kcal());
        assertEquals(0f, capturedFoodRecord.protein());
        assertEquals(3f, capturedFoodRecord.fat());
        assertEquals(10f, capturedFoodRecord.carbohydrate());
    }
}
