package com.tbot.ruler.plugins.agent.mute;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.BinaryClaim;
import com.tbot.ruler.broker.payload.BinaryState;
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

    private final ActuatorState<BinaryState> state;
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
        this.state = ActuatorState.<BinaryState>builder().actuatorUuid(uuid).build();
        initState();
    }

    @Override
    public ActuatorState<BinaryState> getState() {
        refreshState();
        return state;
    }

    @Override
    public void acceptMessage(Message message) {
        consumeMessage(message, BinaryClaim.class, this::consumeBinaryClaimMessage);
    }

    private void consumeBinaryClaimMessage(Message message) {
        BinaryClaim requestedClaim = message.getPayloadAs(BinaryClaim.class);
        BinaryState desiredState = requestedClaim.resolveState(state.getPayload());
        state.updatePayload(desiredState);

        boolean isMute = invertStates ^ desiredState.isOn();
        rulerThingContext.getRulerThingAgent().setOnMute(isMute);
        rulerThingContext.getSubjectStateService().persistState(state);
        log.info("Thing {} onMute flag changed to {}", rulerThingContext.getThingUuid(), isMute);
    }

    private void refreshState() {
        this.state.updatePayload(
                BinaryState.of(invertStates ^ rulerThingContext.getRulerThingAgent().isOnMute()));
    }

    private void initState() {
        ActuatorState<BinaryState> persistedState = rulerThingContext.getSubjectStateService()
            .recoverActuatorState(getUuid(), BinaryState.class);
        if (persistedState != null) {
            state.updatePayload(persistedState.getPayload());
            rulerThingContext.getRulerThingAgent().setOnMute(invertStates ^ persistedState.getPayload().isOn());
        } else {
            refreshState();
        }
    }
}
