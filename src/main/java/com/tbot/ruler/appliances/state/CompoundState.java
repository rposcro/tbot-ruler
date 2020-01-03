package com.tbot.ruler.appliances.state;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class CompoundState implements State {

    private Map<String, ? extends State> componentStates;
}
