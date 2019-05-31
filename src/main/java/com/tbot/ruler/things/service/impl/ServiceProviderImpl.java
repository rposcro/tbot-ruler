package com.tbot.ruler.things.service.impl;

import com.tbot.ruler.rest.RestService;
import com.tbot.ruler.things.service.ServiceProvider;
import com.tbot.ruler.things.service.ThreadRegistrationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component("serviceProvider")
public class ServiceProviderImpl implements ServiceProvider {
    @Autowired
    private RestService restService;
    @Autowired
    private ThreadRegistrationService registrationService;
}
