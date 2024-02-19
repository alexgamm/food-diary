package yacloud;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true, fluent = true)
public class Nlu {
    private List<NluEntity> entities;
    private List<String> tokens;
}
