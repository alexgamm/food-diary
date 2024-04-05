package fooddiary.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record PersonRequest(
        @NotNull String userId,
        @Nullable String firstName,
        @NotNull String request
) {
    public String getPersonId() {
        String personId = userId;
        if (firstName != null && !firstName.isEmpty()) {
            personId = firstName + "/" + personId;
        }
        return Base64.getEncoder().encodeToString(personId.getBytes(StandardCharsets.UTF_8));
    }
}
