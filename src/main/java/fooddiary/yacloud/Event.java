package fooddiary.yacloud;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Event {
    private Request request;
    private Session session;
    private Object version;
}
