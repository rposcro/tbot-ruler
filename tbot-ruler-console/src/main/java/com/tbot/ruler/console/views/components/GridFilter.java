package com.tbot.ruler.console.views.components;

public interface GridFilter<T> {

    boolean test(T t);

    default boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty()
                || value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    static <T> GridFilter<T> passThruFilter() {
        return item -> true;
    }
}
