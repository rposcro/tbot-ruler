package com.tbot.ruler.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tbot.ruler.util.jackson.ZonedDateTimeToStringConverter;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class ReportEntry {

    @JsonSerialize(converter = ZonedDateTimeToStringConverter.class)
    private ZonedDateTime timestamp;

    @JsonEnumDefaultValue
    private ReportEntryLevel entryLevel;

    private String content;
}
