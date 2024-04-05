package fooddiary.yacloud;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Response {
    private Object session;
    private Object version;
    private TextResponse response;
}
