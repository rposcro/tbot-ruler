package com.tbot.layout.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PanelTab {

    private String name;
    private List<Widget> widgets;
}
