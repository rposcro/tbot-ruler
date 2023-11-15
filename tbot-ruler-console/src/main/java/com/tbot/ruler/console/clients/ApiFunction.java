package com.tbot.ruler.console.clients;

import java.io.IOException;

public interface ApiFunction<T> {

    T runProcedure() throws IOException;
}
