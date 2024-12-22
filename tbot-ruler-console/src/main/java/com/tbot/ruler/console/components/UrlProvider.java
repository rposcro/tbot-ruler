package com.tbot.ruler.console.components;

import com.tbot.ruler.console.configuration.RulerServiceClientsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;

@Component
public class UrlProvider {

    @Autowired
    private RulerServiceClientsProperties rulerServiceClientsProperties;

    public String getDownloadZipUrl() {
        URI baseURI = URI.create(rulerServiceClientsProperties.getBaseUrl());
        return new DefaultUriBuilderFactory().builder()
            .host(baseURI.getHost())
            .port(baseURI.getPort())
            .scheme(baseURI.getScheme())
            .path(baseURI.getPath())
            .path("/admin/files/dump/zip")
            .build()
            .toString();
    }
}
