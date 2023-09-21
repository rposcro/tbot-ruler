package com.tbot.ruler.subjects;

import java.util.List;

public interface Thing extends Subject {

    List<? extends Actuator> getActuators();
}
