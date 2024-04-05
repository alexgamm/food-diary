package fooddiary;

import fooddiary.service.SkillService;
import fooddiary.yacloud.*;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SkillTest {

    @Test
    public void invalidCommand_getInvalidCommandResponse() {
        //arrange
        SkillService skill = new SkillService(null, null);
        Event event = new Event()
                .setSession(new Session().setUser(new User().setUser_id("userIdTest")))
                .setRequest(
                        new Request()
                                .setCommand("жаренная картошка 100")
                                .setNlu(new Nlu().setTokens(Collections.emptyList()))
                );
        //act
        Response response = skill.getResponse(event);
        //assert
        assertEquals("Упс, что-то не так с твоим запросом. Скажи еще раз", response.getResponse().getText());
    }

    //TODO tests for commands execution and person request parsing


}
