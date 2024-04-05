package fooddiary.yacloud;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class Nlu {
    private List<NluEntity> entities;
    private List<String> tokens;
}
