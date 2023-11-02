package com.tbot.ruler.util.jackson;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class StringToLocalTimeConverter extends StdConverter<String, LocalTime> {

    @Override
    public LocalTime convert(String time) {
        return LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
    }
}
