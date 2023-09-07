package com.tbot.ruler.plugins.ghost;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import lombok.Builder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

public class SunTimer {

    private SunriseSunsetCalculator calculator;
    private Calendar calendar;
    private ZoneId zoneId;

    @Builder
    public SunTimer(Location location, ZoneId zoneId) {
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
}
