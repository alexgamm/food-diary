package fooddiary.yacloud;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Request {
    private String command;
    private Nlu nlu;
}
