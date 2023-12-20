package com.tbot.ruler.console.views.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Arrays;

public class Prompt extends VerticalLayout {

    public Prompt() {
        setAlignItems(Alignment.CENTER);
        setSizeFull();
    }

    public Prompt(String... promptLines) {
        Arrays.stream(promptLines).forEach(this::addLine);
    }

    public Prompt addLine(String messageLine) {
        add(new Div(new Span(messageLine)));
        return this;
    }
}
