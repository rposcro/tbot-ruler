package com.tbot.ruler.plugins.sunwatch;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

public class SunCalculator {

    private SunriseSunsetCalculator calculator;
    private Calendar calendar;

    private ZoneId zoneId;

    public SunCalculator(SunEventLocale eventLocale) {
        this(eventLocale.getZoneId(), eventLocale.getLocation());
    }

    public SunCalculator(ZoneId zoneId, Location location) {
        TimeZone timeZone = TimeZone.getTimeZone(zoneId);
        this.zoneId = zoneId;
        this.calculator = new SunriseSunsetCalculator(location, timeZone);
        this.calendar = Calendar.getInstance(timeZone);
    }

    public ZonedDateTime sunsetForDate(LocalDate forDate) {
        calendar.set(forDate.getYear(), forDate.getMonthValue() - 1, forDate.getDayOfMonth());
        Calendar eventTime = calculator.getCivilSunsetCalendarForDate(calendar);
        return ZonedDateTime.ofInstant(eventTime.toInstant(), zoneId);
    }

    public ZonedDateTime sunriseForDate(LocalDate forDate) {
        calendar.set(forDate.getYear(), forDate.getMonthValue() - 1, forDate.getDayOfMonth());
        Calendar eventTime = calculator.getCivilSunriseCalendarForDate(calendar);
        return ZonedDateTime.ofInstant(eventTime.toInstant(), zoneId);
    }

    public boolean isNowDaytime(long sunriseShiftMinutes, long sunsetShiftMinutes) {
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        LocalDate today = now.toLocalDate();
        ZonedDateTime sunriseTime = sunriseForDate(today).plusMinutes(sunriseShiftMinutes);
        ZonedDateTime sunsetTime = sunsetForDate(today).plusMinutes(sunsetShiftMinutes);
        return now.isAfter(sunriseTime) && now.isBefore(sunsetTime);
    }

    public boolean isDaytimeAt(ZonedDateTime forDateTime, long sunriseShiftMinutes, long sunsetShiftMinutes) {
        LocalDate today = forDateTime.toLocalDate();
        ZonedDateTime sunriseTime = sunriseForDate(today).plusMinutes(sunriseShiftMinutes);
        ZonedDateTime sunsetTime = sunsetForDate(today).plusMinutes(sunsetShiftMinutes);
        return forDateTime.isAfter(sunriseTime) && forDateTime.isBefore(sunsetTime);
    }
}
