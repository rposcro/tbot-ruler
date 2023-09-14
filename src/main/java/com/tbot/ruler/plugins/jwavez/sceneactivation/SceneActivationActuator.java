package com.tbot.ruler.plugins.jwavez.sceneactivation;

import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.model.BinaryStateClaim;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.messages.MessagePublisher;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(builderClassName = "_SceneActivationEmitterBuilder")
public class SceneActivationActuator implements Actuator {

    @NonNull
    private String id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private MessagePublisher messagePublisher;
    private byte sourceNodeId;
    private byte sceneId;

    @Builder.Default
    private String uniqueSceneKey = null;

    public SceneActivationActuator init() {
        this.uniqueSceneKey = uniqueSceneKey(sourceNodeId, sceneId);
        return this;
    }

    public void publishMessage() {
        messagePublisher.publishMessage(Message.builder()
                .senderId(id)
                .payload(BinaryStateClaim.TOGGLE)
                .build());
    }

    public static String uniqueSceneKey(byte nodeId, byte sceneId) {
        return String.format("%02x-%02x", nodeId, sceneId);
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
    }

    @Override
    public void acceptMessage(Message message) {
    }
}
