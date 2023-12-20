package com.tbot.ruler.broker.payload;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tbot.ruler.util.jackson.ZonedDateTimeToStringConverter;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class ReportLog {

    @JsonSerialize(converter = ZonedDateTimeToStringConverter.class)
    private ZonedDateTime timestamp;

    @JsonEnumDefaultValue
    private ReportLogLevel logLevel;

    private String content;
}
