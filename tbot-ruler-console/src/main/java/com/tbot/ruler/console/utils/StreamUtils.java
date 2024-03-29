package com.tbot.ruler.console.utils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StreamUtils {

    public static <K, E> Map<K, E> toMap(Collection<E> collection, Function<E, K> keyMapper) {
        return collection.stream().collect(Collectors.toMap(keyMapper::apply, Function.identity()));
    }

    public static <E> E anyMatching(Collection<E> collection, Predicate<E> predicate) {
        return collection.stream().filter(predicate).findFirst().orElse(null);
    }
}
