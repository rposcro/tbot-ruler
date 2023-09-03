package com.tbot.ruler.plugins.sunwatch.sunevent;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

import com.luckycatlabs.sunrisesunset.dto.Location;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

@FunctionalInterface
public interface SunEventTimer {

    ZonedDateTime forDate(LocalDate forDate);

    static SunEventTimerContext context(ZoneId zoneId, Location location) {
        SunEventTimerContext context = new SunEventTimerContext();
        TimeZone timeZone = TimeZone.getTimeZone(zoneId);
        context.calendar = Calendar.getInstance(timeZone);
        context.calendar.set(0, 0, 0, 0,0,0);
        context.calculator = new SunriseSunsetCalculator(location, timeZone);
        return context;
    }

    class SunEventTimerContext {
        private SunriseSunsetCalculator calculator;
        private Calendar calendar;
    }
}
