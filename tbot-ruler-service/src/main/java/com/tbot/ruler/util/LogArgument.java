package com.tbot.ruler.util;

import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public class LogArgument {

    private Supplier<String> supplier;

    public static LogArgument argument(Supplier<String> supplier) {
        return new LogArgument(supplier);
    }

    @Override
    public String toString() {
        return supplier == null ? null : supplier.get();
    }
}
