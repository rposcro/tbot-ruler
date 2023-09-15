package com.tbot.ruler.plugins;

import com.tbot.ruler.things.Thing;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Builder
@Getter
public class BasicPlugin implements Plugin {

    private String uuid;
    private String name;
    @Builder.Default
    private List<? extends Thing> things = Collections.emptyList();
}
