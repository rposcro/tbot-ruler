package com.tbot.ruler.plugins.sunwatch;

import com.luckycatlabs.sunrisesunset.dto.Location;
import lombok.Builder;
import lombok.Getter;

import java.time.ZoneId;

@Builder
@Getter
public class SunLocale {
    private Location location;
    private ZoneId zoneId;
}
