package fooddiary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RequiredArgsConstructor
@Getter
public class PersonRequest {
    @NotNull
    private final String userId;
    @Nullable
    private final String firstName;
    @NotNull
    private final String request;

    public String getPersonId() {
        String personId = userId;
        if (firstName != null && !firstName.isEmpty()) {
            personId = firstName + "/" + personId;
        }
        return Base64.getEncoder().encodeToString(personId.getBytes(StandardCharsets.UTF_8));
    }
}
