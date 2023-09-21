package com.tbot.ruler.subjects;

import com.tbot.ruler.subjects.Subject;
import com.tbot.ruler.subjects.Thing;

import java.util.List;

public interface Plugin extends Subject {

    List<? extends Thing> getThings();
}
