package com.tbot.ruler.plugins.ghost;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class TimeRange {

    private LocalTime startTime;
    private LocalTime endTime;
    private boolean spansDays;

    public TimeRange(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.spansDays = startTime.compareTo(endTime) >= 0;
    }

    public boolean isInRange(LocalDateTime theDateTime) {
        return isInRange(theDateTime.toLocalTime());
    }

    public boolean isInRange(LocalTime theTime) {
        if (spansDays) {
            return theTime.compareTo(endTime) <= 0 || theTime.compareTo(startTime) >= 0;
        } else {
            return theTime.compareTo(startTime) >= 0 && theTime.compareTo(endTime) <= 0;
        }
    }

    public LocalDateTime nextTimeInRange(LocalDateTime biasDate) {
        if (isInRange(biasDate.toLocalTime())) {
            return biasDate;
        } else if (spansDays) {
            return biasDate.toLocalDate().atTime(startTime);
        } else if (biasDate.toLocalTime().compareTo(startTime) <= 0) {
            return biasDate.toLocalDate().atTime(startTime);
        } else {
            return biasDate.toLocalDate().plusDays(1).atTime(startTime);
        }
    }
}
