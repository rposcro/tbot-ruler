package com.tbot.ruler.plugins.ghost;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DateTimeRange {

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public DateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public boolean isInRange(LocalDateTime theDateTime) {
        return theDateTime.compareTo(startDateTime) >= 0 && theDateTime.compareTo(endDateTime) <= 0;
    }
}
