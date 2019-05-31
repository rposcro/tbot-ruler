package com.tbot.ruler.things;

public class ActuatorException extends ThingException {
    public ActuatorException(Throwable cause) {
        super(cause);
    }

    public ActuatorException(String m, Throwable t) {
        super(m, t);
    }

    public ActuatorException(String m) {
        super(m);
    }
}
