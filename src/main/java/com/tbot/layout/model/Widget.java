package com.tbot.layout.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Widget {

    private String id;
    private String type;
    private String name;
    private String description;
    private List<WidgetComponent> components;
}
