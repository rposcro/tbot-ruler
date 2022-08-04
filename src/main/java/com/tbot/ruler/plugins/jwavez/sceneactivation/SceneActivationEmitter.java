package com.tbot.ruler.plugins.jwavez.sceneactivation;

import com.tbot.ruler.message.DeliveryReport;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.payloads.BooleanTogglePayload;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.message.MessagePublisher;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(builderClassName = "_SceneActivationEmitterBuilder")
public class SceneActivationEmitter implements Emitter {

    @NonNull private String id;
    @NonNull private String name;
    @NonNull private String description;
    @NonNull private MessagePublisher messagePublisher;
    private byte sourceNodeId;
    private byte sceneId;

    @Builder.Default
    private String uniqueSceneKey = null;
    @Builder.Default
    private Message message = null;

    public SceneActivationEmitter init() {
        this.message = Message.builder()
            .senderId(id)
            .payload(BooleanTogglePayload.TOGGLE_PAYLOAD)
            .build();
        this.uniqueSceneKey = uniqueSceneKey(sourceNodeId, sceneId);
        return this;
    }

    public void publishMessage() {
        messagePublisher.acceptMessage(message);
    }

    public static String uniqueSceneKey(byte nodeId, byte sceneId) {
        return String.format("%02x-%02x", nodeId, sceneId);
    }

    @Override
    public void acceptDeliveryReport(DeliveryReport deliveryReport) {
    }
}
