package fooddiary.yacloud;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TextResponse {

    private String text;
    private boolean end_session;
}
