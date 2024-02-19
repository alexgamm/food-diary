package fooddiary.command;

import fooddiary.PersonRequest;

public interface Command {

    String getResponse(PersonRequest personRequest);

    boolean isRelevant(String request);
}
