package com.tbot.ruler.util;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionsUtil {

    public static <T> Set<T> asSet(T... items) {
        return Stream.of(items).collect(Collectors.toSet());
    }
}
