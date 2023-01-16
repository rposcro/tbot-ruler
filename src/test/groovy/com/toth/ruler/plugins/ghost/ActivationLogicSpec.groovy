package com.toth.ruler.plugins.ghost


import com.tbot.ruler.plugins.ghost.RandomActuatorConfiguration

import spock.lang.Specification

class ActivationLogicSpec extends Specification {

    Random randomTimeGenerator;

    def setup() {
        def randomTimeGenerator = Stub(Random.class);
    }

    def "Should draw activation period"() {
        given:
        def configuration = RandomActuatorConfiguration.builder()
                .minBreakTime(50)
                .maxBreakTime(100)
                .build();
        def activationLogic = new ActivationLogic(configuration);
        activationLogic.randomTimeGenerator = this.randomTimeGenerator;

        and:
        randomTimeGenerator.nextLong() >> 160;

        when:
        long period = activationLogic.drawActivationPeriod();

        then:
        period == 10;
    }
}
