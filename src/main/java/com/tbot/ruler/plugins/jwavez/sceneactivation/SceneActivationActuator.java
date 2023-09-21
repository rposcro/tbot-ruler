package com.tbot.ruler.plugins.jwavez.sceneactivation;

import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.BinaryStateClaim;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.broker.MessagePublisher;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(builderClassName = "_SceneActivationEmitterBuilder")
public class SceneActivationActuator implements Actuator {

    @NonNull
    private String uuid;
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
                .senderId(uuid)
                .payload(BinaryStateClaim.TOGGLE)
                .build());
    }

    public static String uniqueSceneKey(byte nodeId, byte sceneId) {
        return String.format("%02x-%02x", nodeId, sceneId);
    }

    @Override
    public void acceptPublicationReport(MessagePublicationReport publicationReport) {
    }

    @Override
    public void acceptMessage(Message message) {
    }
}
