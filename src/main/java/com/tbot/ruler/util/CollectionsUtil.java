package com.tbot.ruler.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionsUtil {

    public static <T> Set<T> asSet(T... ts) {
        return Stream.of(ts).collect(Collectors.toSet());
    }

    public static <T> Collection<T> orEmpty(Collection<T> collection) {
        return collection != null ? collection : Collections.emptyList();
    }

    public static <T> List<T> orEmpty(List<T> collection) {
        return collection != null ? collection : Collections.emptyList();
    }
}
