package com.tbot.ruler.plugins.jwavez;

import com.tbot.ruler.signals.EmitterSignal;
import com.tbot.ruler.signals.OnOffSignalValue;
import com.tbot.ruler.signals.SignalValue;
import com.tbot.ruler.signals.ToggleSignalValue;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.EmitterId;
import com.tbot.ruler.things.EmitterMetadata;
import lombok.Builder;
import lombok.Getter;

import java.util.function.Consumer;

@Getter
@Builder
public class BasicSetEmitter implements Emitter {

    private EmitterId id;
    private EmitterMetadata metadata;
    private Consumer<EmitterSignal> signalConsumer;
    private BasicSetValueMode valueMode;
    private byte sourceNodeId;
    private byte turnOnValue;
    private byte turnOffValue;

    public void activateSignal(byte commandValue) {
        signalConsumer.accept(new EmitterSignal(signalValue(commandValue), id));
    }

    private SignalValue signalValue(byte commandValue) {
        switch(valueMode) {
            case TOGGLE_VALUE:
                return new ToggleSignalValue();
            case ON_OFF_VALUES:
                return commandValue == turnOnValue ? OnOffSignalValue.ON_SIGNAL_VALUE : OnOffSignalValue.OFF_SIGNAL_VALUE;
            default:
                throw new IllegalStateException("Unexpected implementation inconsistency!");
        }
    }

    public BasicSetEmitter init() {
        return this;
    }
}
