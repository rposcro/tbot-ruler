package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.BinaryClaim;
import com.tbot.ruler.jobs.Job;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.scheduling.support.CronExpression;

import java.time.Clock;
import java.time.ZonedDateTime;

public class CronEmissionJob implements Job {

    private final Message onMessage;
    private final Message offMessage;
    private final CronExpression switchOnExpression;
    private final CronExpression switchOffExpression;
    private final String jobName;
    private final MessagePublisher messagePublisher;
    private final Clock clock;

    private boolean currentState;
    private ZonedDateTime lastSwitchTime;

    @Builder
    public CronEmissionJob(
        boolean defaultState,
        @NonNull String actuatorUuid,
        @NonNull MessagePublisher messagePublisher,
        @NonNull String switchOnSchedule,
        @NonNull String switchOffSchedule,
        Clock clock)
    {
        this.onMessage = Message.builder().senderId(actuatorUuid).payload(BinaryClaim.SET_ON).build();
        this.offMessage = Message.builder().senderId(actuatorUuid).payload(BinaryClaim.SET_OFF).build();
        this.switchOnExpression = CronExpression.parse(switchOnSchedule);
        this.switchOffExpression = CronExpression.parse(switchOffSchedule);
        this.jobName = "CronEmission@" + actuatorUuid;
        this.messagePublisher = messagePublisher;
        this.clock = clock != null ? clock : Clock.systemDefaultZone();
        this.currentState = defaultState;
        this.lastSwitchTime = ZonedDateTime.now(this.clock);
    }

    @Override
    public void doJob() throws InterruptedException {
        currentState = resolveCurrentState(currentState, ZonedDateTime.now(clock));
        messagePublisher.publishMessage(currentState ? onMessage : offMessage);
    }

    private boolean resolveCurrentState(boolean initialState, ZonedDateTime nowDateTime) {
        ZonedDateTime cursor = lastSwitchTime;
        boolean resolvedState = initialState;

        while (true) {
            ZonedDateTime nextOn = switchOnExpression.next(cursor);
            ZonedDateTime nextOff = switchOffExpression.next(cursor);
            ZonedDateTime nextSwitchTime = earliest(nextOn, nextOff);

            if (nextSwitchTime == null || nextSwitchTime.isAfter(nowDateTime)) {
                break;
            }

            // In case both expressions match the same instant, OFF wins deterministically.
            resolvedState = nextOff == null || !nextOff.equals(nextSwitchTime);

            cursor = nextSwitchTime;
        }

        lastSwitchTime = cursor;
        return resolvedState;
    }

    private static ZonedDateTime earliest(ZonedDateTime first, ZonedDateTime second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        return first.isBefore(second) ? first : second;
    }

    @Override
    public String getJobName() {
        return jobName;
    }
}
