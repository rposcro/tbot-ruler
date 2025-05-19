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

import java.util.Optional;

@Slf4j
@Getter
public class ThingMuteActuator extends AbstractActuator {

    private final RulerThingContext rulerThingContext;
    private final boolean invertStates;

    private ActuatorState<BinaryState> state;

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
        initState();
    }

    @Override
    public ActuatorState<BinaryState> getState() {
        this.state.updatePayload(getThingAgentState());
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

    private void initState() {
        Optional<ActuatorState<BinaryState>> persistedState = rulerThingContext.getSubjectStateService()
            .recoverActuatorState(getUuid(), BinaryState.class);

        if (persistedState.isPresent()) {
            this.state = persistedState.get();
            rulerThingContext.getRulerThingAgent().setOnMute(invertStates ^ state.getPayload().isOn());
        } else {
            this.state = ActuatorState.of(uuid, getThingAgentState());
        }
    }

    private BinaryState getThingAgentState() {
        return BinaryState.of(invertStates ^ rulerThingContext.getRulerThingAgent().isOnMute());
    }
}
