package com.tbot.ruler.util.jackson;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeToStringConverter extends StdConverter<ZonedDateTime, String> {

    @Override
    public String convert(ZonedDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
