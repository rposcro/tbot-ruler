package com.tbot.ruler.plugins.sunwatch;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import lombok.Builder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

public class SunCalculator {

    private SunriseSunsetCalculator calculator;
    private Calendar calendar;
    private ZoneId zoneId;
    private long sunriseShiftMinutes;
    private long sunsetShiftMinutes;

    @Builder
    public SunCalculator(SunEventLocale eventLocale, long sunriseShiftMinutes, long sunsetShiftMinutes) {
        TimeZone timeZone = TimeZone.getTimeZone(eventLocale.getZoneId());
        this.zoneId = eventLocale.getZoneId();
        this.calculator = new SunriseSunsetCalculator(eventLocale.getLocation(), timeZone);
        this.calendar = Calendar.getInstance(timeZone);
        this.sunriseShiftMinutes = sunriseShiftMinutes;
        this.sunsetShiftMinutes = sunsetShiftMinutes;
    }

    public ZonedDateTime sunsetForDate(LocalDate forDate) {
        calendar.set(forDate.getYear(), forDate.getMonthValue() - 1, forDate.getDayOfMonth());
        Calendar eventTime = calculator.getCivilSunsetCalendarForDate(calendar);
        return ZonedDateTime.ofInstant(eventTime.toInstant(), zoneId).plusMinutes(sunsetShiftMinutes);
    }

    public ZonedDateTime sunriseForDate(LocalDate forDate) {
        calendar.set(forDate.getYear(), forDate.getMonthValue() - 1, forDate.getDayOfMonth());
        Calendar eventTime = calculator.getCivilSunriseCalendarForDate(calendar);
        return ZonedDateTime.ofInstant(eventTime.toInstant(), zoneId).plusMinutes(sunriseShiftMinutes);
    }

    public ZonedDateTime nextSunrise() {
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        LocalDate today = now.toLocalDate();
        ZonedDateTime sunriseToday = sunriseForDate(today);
        return sunriseToday.compareTo(now) >= 0 ? sunriseToday : sunriseForDate(today.plusDays(1));
    }

    public ZonedDateTime nextSunset() {
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        LocalDate today = now.toLocalDate();
        ZonedDateTime sunsetToday = sunsetForDate(today);
        return sunsetToday.compareTo(now) >= 0 ? sunsetToday : sunsetForDate(today.plusDays(1));
    }

    public boolean isDaytimeNow() {
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        LocalDate today = now.toLocalDate();
        ZonedDateTime sunriseTime = sunriseForDate(today).plusMinutes(sunriseShiftMinutes);
        ZonedDateTime sunsetTime = sunsetForDate(today).plusMinutes(sunsetShiftMinutes);
        return now.isAfter(sunriseTime) && now.isBefore(sunsetTime);
    }

    public boolean isDaytimeAt(ZonedDateTime forDateTime) {
        LocalDate today = forDateTime.toLocalDate();
        ZonedDateTime sunriseTime = sunriseForDate(today).plusMinutes(sunriseShiftMinutes);
        ZonedDateTime sunsetTime = sunsetForDate(today).plusMinutes(sunsetShiftMinutes);
        return forDateTime.isAfter(sunriseTime) && forDateTime.isBefore(sunsetTime);
    }
}
