package com.tbot.ruler.plugins.agent.mute;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.subjects.actuator.AbstractActuator;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ThingMuteActuator extends AbstractActuator {

    private final ActuatorState<OnOffState> state;
    private final RulerThingContext rulerThingContext;
    private final boolean invertStates;

    @Builder
    public ThingMuteActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            @NonNull RulerThingContext rulerThingContext,
            @NonNull ThingMuteActuatorConfiguration configuration) {
        super(uuid, name, description);
        this.rulerThingContext = rulerThingContext;
        this.invertStates = configuration.isInvertStates();
        this.state = ActuatorState.<OnOffState>builder().actuatorUuid(uuid).build();
        refreshState();
    }

    @Override
    public ActuatorState<OnOffState> getState() {
        refreshState();
        return state;
    }

    @Override
    public void acceptMessage(Message message) {
        OnOffState requestedState = message.getPayloadAs(OnOffState.class);
        state.updatePayload(requestedState);

        boolean isMute = invertStates ^ requestedState.isOn();
        rulerThingContext.getRulerThingAgent().setOnMute(isMute);
        log.info("Thing {} onMute flag changed to {}", rulerThingContext.getThingUuid(), isMute);
    }

    private void refreshState() {
        this.state.updatePayload(
                OnOffState.of(invertStates ^ rulerThingContext.getRulerThingAgent().isOnMute()));
    }
}
