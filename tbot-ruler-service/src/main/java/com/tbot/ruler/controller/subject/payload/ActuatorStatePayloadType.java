package com.tbot.ruler.controller.subject.payload;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ActuatorStatePayloadType {

    Object,
    OnOff,
    Rgbw,
    Trigger;

    private static Map<String, ActuatorStatePayloadType> typesMap = Stream.of(ActuatorStatePayloadType.values())
            .collect(Collectors.toMap(ActuatorStatePayloadType::name, Function.identity()));

    public static Optional<ActuatorStatePayloadType> fromString(String typeName) {
        return Optional.ofNullable(typesMap.get(typeName));
    }
}
