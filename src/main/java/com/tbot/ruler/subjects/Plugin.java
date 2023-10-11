package com.tbot.ruler.subjects;

import java.util.List;

public interface Plugin extends Subject {

    List<? extends Thing> getThings();
}
