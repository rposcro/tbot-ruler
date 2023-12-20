package com.tbot.ruler.console.clients;

import java.io.IOException;

public interface ApiProcedure<T> {

    void runProcedure() throws IOException;
}
