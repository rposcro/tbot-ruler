package com.tbot.ruler.things.exceptions;

public class CollectorException extends ThingException {
    public CollectorException(Throwable cause) {
        super(cause);
    }

    public CollectorException(String m, Throwable t) {
        super(m, t);
    }

    public CollectorException(String m) {
        super(m);
    }
}
