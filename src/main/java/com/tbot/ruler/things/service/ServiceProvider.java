package com.tbot.ruler.things.service;

import com.tbot.ruler.rest.RestService;

public interface ServiceProvider {
    public RestService getRestService();
    public ThreadRegistrationService getRegistrationService();
}
