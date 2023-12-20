package com.tbot.ruler.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

public class DateUtil {
    public static ZonedDateTime zonedDateTime(Calendar calendar, ZoneId zoneId) {
        return ZonedDateTime.ofInstant(calendar.toInstant(), zoneId);
    }
}
