package com.tbot.ruler.plugins.sunwatch

import com.luckycatlabs.sunrisesunset.dto.Location
import spock.lang.Specification

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class SunEventSpec extends Specification {

    def "civil sunrise calculated" () {
        given:
        SunEventTimer civilSunrise = SunEventTimer.forCivilSunrise(ZoneId.of("Europe/Warsaw"), new Location("50.017483", "19.870309"));

        when:
        ZonedDateTime sunriseTime = civilSunrise.forDate(LocalDate.of(1976, 4, 14));

        then:
        sunriseTime.year == 1976;
        sunriseTime.monthValue == 4;
        sunriseTime.dayOfMonth == 14;
        sunriseTime.hour == 4;
        sunriseTime.minute == 14;
        sunriseTime.second == 0;
    }

    def "civil sunset calculated" () {
        given:
        SunEventTimer civilSunset = SunEventTimer.forCivilSunset(ZoneId.of("Europe/Warsaw"), new Location("50.017483", "19.870309"));

        when:
        ZonedDateTime sunsetTime = civilSunset.forDate(LocalDate.of(1976, 4, 14));

        then:
        sunsetTime.year == 1976;
        sunsetTime.monthValue == 4;
        sunsetTime.dayOfMonth == 14;
        sunsetTime.hour == 19;
        sunsetTime.minute == 8;
        sunsetTime.second == 0;
    }
}
