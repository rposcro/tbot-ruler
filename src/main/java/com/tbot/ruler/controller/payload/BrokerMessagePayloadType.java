package com.tbot.ruler.controller.payload;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum BrokerMessagePayloadType {
    Object,
    OnOff,
    Rgbw;

    private static Map<String, BrokerMessagePayloadType> typesMap = Stream.of(BrokerMessagePayloadType.values())
            .collect(Collectors.toMap(BrokerMessagePayloadType::name, Function.identity()));

    public static Optional<BrokerMessagePayloadType> fromString(String typeName) {
        return Optional.ofNullable(typesMap.get(typeName));
    }
}
