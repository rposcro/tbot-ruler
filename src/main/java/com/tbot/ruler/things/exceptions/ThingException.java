package com.tbot.ruler.things.exceptions;

public class ThingException extends Exception {

    public ThingException(Throwable t) {
        super(t);
    }

    public ThingException(String m, Throwable t) {
        super(m, t);
    }

    public ThingException(String m) {
        super(m);
    }
}
