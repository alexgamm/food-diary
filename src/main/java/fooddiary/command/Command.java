package fooddiary.command;

import fooddiary.model.PersonRequest;

public interface Command {

    String getResponse(PersonRequest personRequest);

    boolean isRelevant(String request);
}
