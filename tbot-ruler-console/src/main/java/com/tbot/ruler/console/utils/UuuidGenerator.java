package com.tbot.ruler.console.utils;

import com.vaadin.flow.spring.annotation.SpringComponent;

import java.util.UUID;

@SpringComponent
public class UuuidGenerator {

    private final static String UUID_PREFIX_PLUGIN = "plgn-";

    public String generatePluginUuid() {
        return UUID_PREFIX_PLUGIN + generateUuid();
    }

    public String generateUuid() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    public static void main(String... args) {
        System.out.println(new UuuidGenerator().generatePluginUuid());
    }
}
