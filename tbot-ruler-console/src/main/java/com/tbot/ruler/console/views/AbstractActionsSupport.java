package com.tbot.ruler.console.views;

import com.tbot.ruler.console.exceptions.ClientCommunicationException;

import static com.tbot.ruler.console.views.PopupNotifier.promptError;

public abstract class AbstractActionsSupport {

    protected boolean handlingExceptions(Runnable procedure) {
        try {
            procedure.run();
            return true;
        } catch(ClientCommunicationException e) {
            promptError(e.getMessage(), e.getResponseMessage());
        } catch(Exception e) {
            promptError("Something's wrong, check logs for details!", e.getMessage());
        }
        return false;
    }
}
