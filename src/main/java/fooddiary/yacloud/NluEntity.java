package fooddiary.yacloud;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NluEntity {
    private String type;
    private Object value;
    private Tokens tokens;

    @Data
    @Accessors(chain = true)
    public static class Tokens {
        private int start;
        private int end;
    }
}
