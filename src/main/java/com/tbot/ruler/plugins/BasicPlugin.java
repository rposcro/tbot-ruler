package com.tbot.ruler.plugins;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BasicPlugin implements Plugin {

    private String uuid;
    private String name;
}
