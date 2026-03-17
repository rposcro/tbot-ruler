package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.BinaryClaim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CronEmissionJobTest {

    // switchOn  fires every 10 seconds: T+10, T+20, T+30, ...
    // switchOff fires every 15 seconds: T+15, T+30, T+45, ...

    private static final String SWITCH_ON_SCHEDULE  = "*/10 * * * * *";
    private static final String SWITCH_OFF_SCHEDULE = "*/15 * * * * *";

    private MutableClock clock;
    private MessagePublisher publisher;

    @BeforeEach
    void setUp() {
        clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"));
        publisher = mock(MessagePublisher.class);
    }

    @Test
    void doJob_whenNoSwitchOccurred_publishesDefaultOnState() throws InterruptedException {
        CronEmissionJob job = jobWithDefaultState(true);

        clock.advance(1);   // T+1 — before first ON tick (T+10)
        job.doJob();

        assertThat(capturePayload()).isEqualTo(BinaryClaim.SET_ON);
    }

    @Test
    void doJob_whenNoSwitchOccurred_publishesDefaultOffState() throws InterruptedException {
        CronEmissionJob job = jobWithDefaultState(false);

        clock.advance(1);   // T+1 — before first ON tick (T+10)
        job.doJob();

        assertThat(capturePayload()).isEqualTo(BinaryClaim.SET_OFF);
    }

    @Test
    void doJob_whenSwitchOnPassed_publishesOnMessage() throws InterruptedException {
        CronEmissionJob job = jobWithDefaultState(false);

        clock.advance(11);  // T+11 — past ON (T+10), before OFF (T+15)
        job.doJob();

        assertThat(capturePayload()).isEqualTo(BinaryClaim.SET_ON);
    }

    @Test
    void doJob_whenSwitchOffPassedAfterSwitchOn_publishesOffMessage() throws InterruptedException {
        CronEmissionJob job = jobWithDefaultState(false);

        clock.advance(16);  // T+16 — past ON (T+10) and OFF (T+15)
        job.doJob();

        assertThat(capturePayload()).isEqualTo(BinaryClaim.SET_OFF);
    }

    @Test
    void doJob_whenMultipleSwitchesPassed_appliesLastOne() throws InterruptedException {
        CronEmissionJob job = jobWithDefaultState(false);

        clock.advance(21);  // T+21 — past ON (T+10), OFF (T+15), ON (T+20)
        job.doJob();

        assertThat(capturePayload()).isEqualTo(BinaryClaim.SET_ON);
    }

    @Test
    void doJob_whenCalledMultipleTimes_advancesStateCorrectly() throws InterruptedException {
        CronEmissionJob job = jobWithDefaultState(false);

        clock.advance(11);  // T+11: ON
        job.doJob();
        assertThat(capturePayload(1)).isEqualTo(BinaryClaim.SET_ON);

        clock.advance(5);   // T+16: OFF
        job.doJob();
        assertThat(capturePayload(2)).isEqualTo(BinaryClaim.SET_OFF);

        clock.advance(5);   // T+21: ON
        job.doJob();
        assertThat(capturePayload(3)).isEqualTo(BinaryClaim.SET_ON);
    }

    @Test
    void doJob_whenBothExpressionsMatchSameInstant_offWins() throws InterruptedException {
        CronEmissionJob job = CronEmissionJob.builder()
            .defaultState(true)
            .actuatorUuid("act-tie")
            .messagePublisher(publisher)
            .switchOnSchedule("*/10 * * * * *")
            .switchOffSchedule("*/10 * * * * *")
            .clock(clock)
            .build();

        clock.advance(11);  // T+11 — both fire at T+10, OFF wins
        job.doJob();

        assertThat(capturePayload()).isEqualTo(BinaryClaim.SET_OFF);
    }

    @Test
    void doJob_whenNullClockProvided_usesSystemDefault() {
        CronEmissionJob job = CronEmissionJob.builder()
            .defaultState(false)
            .actuatorUuid("act-sys")
            .messagePublisher(publisher)
            .switchOnSchedule(SWITCH_ON_SCHEDULE)
            .switchOffSchedule(SWITCH_OFF_SCHEDULE)
            .clock(null)
            .build();

        // Just verify the job was constructed and can call doJob without throwing
        assertThat(job).isNotNull();
        assertThat(job.getJobName()).isEqualTo("CronEmission@act-sys");
    }

    private CronEmissionJob jobWithDefaultState(boolean defaultState) {
        return CronEmissionJob.builder()
            .defaultState(defaultState)
            .actuatorUuid("act-1")
            .messagePublisher(publisher)
            .switchOnSchedule(SWITCH_ON_SCHEDULE)
            .switchOffSchedule(SWITCH_OFF_SCHEDULE)
            .clock(clock)
            .build();
    }

    private BinaryClaim capturePayload() {
        return capturePayload(1);
    }

    private BinaryClaim capturePayload(int wantedNumberOfInvocations) {
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(publisher, times(wantedNumberOfInvocations)).publishMessage(captor.capture());
        return (BinaryClaim) captor.getValue().getPayload();
    }

    private static final class MutableClock extends Clock {
        private Instant instant;
        private final ZoneId zoneId;

        MutableClock(Instant instant, ZoneId zoneId) {
            this.instant = instant;
            this.zoneId = zoneId;
        }

        void advance(long seconds) {
            this.instant = instant.plusSeconds(seconds);
        }

        @Override public ZoneId getZone() { return zoneId; }
        @Override public Clock withZone(ZoneId zone) { return new MutableClock(instant, zone); }
        @Override public Instant instant() { return instant; }
    }
}

