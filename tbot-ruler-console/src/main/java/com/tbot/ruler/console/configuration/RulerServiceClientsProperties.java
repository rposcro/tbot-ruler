package com.tbot.ruler.console.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("client.ruler-service")
public class RulerServiceClientsProperties {

    private String baseUrl;

    private int timeoutInMilliseconds;

    private int maxIdleConnections;
}
