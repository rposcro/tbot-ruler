package com.tbot.ruler.plugins.jwavez;

import com.tbot.ruler.signals.EmitterSignal;
import com.tbot.ruler.signals.ToggleSignalValue;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.EmitterId;
import com.tbot.ruler.things.EmitterMetadata;
import lombok.Builder;
import lombok.Getter;

import java.util.function.Consumer;

@Getter
@Builder
public class SceneActivationEmitter implements Emitter {

    private EmitterId id;
    private EmitterMetadata metadata;
    private Consumer<EmitterSignal> signalConsumer;
    private byte sourceNodeId;
    private byte sceneId;

    private String uniqueSceneKey;
    private EmitterSignal signal;

    public void activateSignal() {
        signalConsumer.accept(signal);
    }

    public SceneActivationEmitter init() {
        uniqueSceneKey = uniqueSceneKey(sourceNodeId, sceneId);
        signal = new EmitterSignal(new ToggleSignalValue(), id);
        return this;
    }

    public static String uniqueSceneKey(byte nodeId, byte sceneId) {
        return String.format("%02x-%02x", nodeId, sceneId);
    }
}
