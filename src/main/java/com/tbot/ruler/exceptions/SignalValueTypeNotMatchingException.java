package com.tbot.ruler.exceptions;

public class SignalValueTypeNotMatchingException extends SignalException {

    public SignalValueTypeNotMatchingException(String message) {
        super(message);
    }

    public SignalValueTypeNotMatchingException(String messageFormat, Object... params) {
        super(String.format(messageFormat, params));
    }
}
