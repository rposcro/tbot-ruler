package com.tbot.ruler.subjects.service.impl;

import com.tbot.ruler.rest.RestService;
import com.tbot.ruler.subjects.service.ServiceProvider;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component("serviceProvider")
public class ServiceProviderImpl implements ServiceProvider {

    @Autowired
    private RestService restService;
}
