package yacloud;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
public class NluEntity {
    private String type;
    private Object value;
    private Tokens tokens;

    @Data
    @Accessors(chain = true, fluent = true)
    public static class Tokens {
        private int start;
        private int end;
    }
}
