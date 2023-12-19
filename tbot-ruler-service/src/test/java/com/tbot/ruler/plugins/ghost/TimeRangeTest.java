package com.tbot.ruler.plugins.ghost;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.LocalTime.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TimeRangeTest {

    @Test
    public void inRangeTestForNonSpannigDays() {
        TimeRange range = new TimeRange(of(10, 0), of(14, 46));

        assertFalse(range.isInRange(LocalTime.MIN));
        assertFalse(range.isInRange(of(9, 59)));
        assertTrue(range.isInRange(of(10, 0)));
        assertTrue(range.isInRange(of(12, 0)));
        assertTrue(range.isInRange(of(14, 46)));
        assertFalse(range.isInRange(of(14, 46, 1)));
        assertFalse(range.isInRange(LocalTime.MAX));
    }

    @Test
    public void inRangeTestForSpannigDays() {
        TimeRange range = new TimeRange(of(20, 0), of(6, 30));

        assertTrue(range.isInRange(LocalTime.MIN));
        assertTrue(range.isInRange(of(5, 12)));
        assertTrue(range.isInRange(of(6, 30)));
        assertFalse(range.isInRange(of(6, 30, 0, 10)));
        assertFalse(range.isInRange(of(14, 46)));
        assertTrue(range.isInRange(of(20, 0, 0)));
        assertTrue(range.isInRange(LocalTime.MAX));
    }

    @Test
    public void nextTimeInRangeTestForNonSpannigDays() {
        TimeRange range = new TimeRange(of(10, 0), of(14, 0));

        assertEquals(dateTime(10, 0), range.nextTimeInRange(dateTime(6, 12)));
        assertEquals(dateTime(10, 5), range.nextTimeInRange(dateTime(10, 5)));
        assertEquals(dateTime(10, 0).plusDays(1), range.nextTimeInRange(dateTime(15, 0)));
    }

    @Test
    public void nextTimeInRangeTestForSpannigDays() {
        TimeRange range = new TimeRange(of(20, 15), of(6, 30));

        assertEquals(dateTime(4, 13), range.nextTimeInRange(dateTime(4, 13)));
        assertEquals(dateTime(20, 15), range.nextTimeInRange(dateTime(13, 50)));
        assertEquals(dateTime(21, 25), range.nextTimeInRange(dateTime(21, 25)));
    }

    private LocalDateTime dateTime(int hour, int minute) {
        return LocalDateTime.of(2022, 9, 11, hour, minute);
    }
}
