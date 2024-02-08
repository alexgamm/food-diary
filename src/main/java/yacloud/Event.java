package yacloud;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
public class Event {
    private Request request;
    private Object session;
    private Object version;
}
