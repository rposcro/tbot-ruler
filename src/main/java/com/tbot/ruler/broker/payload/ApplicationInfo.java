package com.tbot.ruler.broker.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplicationInfo {

    private String appName;
    private String appVersion;
}
