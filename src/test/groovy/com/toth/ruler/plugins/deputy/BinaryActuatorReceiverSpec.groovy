package com.toth.ruler.plugins.deputy

import com.tbot.ruler.exceptions.MessageProcessingException
import com.tbot.ruler.exceptions.RestRequestException
import com.tbot.ruler.message.Message
import com.tbot.ruler.message.MessagePublisher
import com.tbot.ruler.message.payloads.BooleanTogglePayload
import com.tbot.ruler.message.payloads.BooleanUpdatePayload
import com.tbot.ruler.message.payloads.UpdateRequestPayload
import com.tbot.ruler.plugins.deputy.BinaryActuatorReceiver
import com.tbot.ruler.plugins.deputy.model.BinOutState
import com.tbot.ruler.rest.RestGetCommand
import com.tbot.ruler.rest.RestPatchCommand
import com.tbot.ruler.rest.RestResponse
import com.tbot.ruler.things.ActuatorId
import com.tbot.ruler.things.ApplianceId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Unroll

class BinaryActuatorReceiverSpec extends Specification {

    ActuatorId actuatorId;
    MessagePublisher messagePublisher;
    RestPatchCommand patchCommand;
    RestGetCommand getCommand;
    BinaryActuatorReceiver actuatorReceiver;

    def setup() {
        actuatorId = new ActuatorId("12");
        messagePublisher = Mock();
        patchCommand = Mock(constructorArgs: ["", "", "", 0, 0, 0]);
        getCommand = Mock(constructorArgs: ["", "", "", 0, 0, 0]);
        actuatorReceiver = BinaryActuatorReceiver.builder()
            .patchCommand(patchCommand)
            .getCommand(getCommand)
            .messagePublisher(messagePublisher)
            .actuatorId(actuatorId)
            .build();
    }

    @Unroll
    def "Boolean update payload handled properly for #state == #param"() {
        given:
        def message = Message.builder()
            .senderId(new ApplianceId("100"))
            .payload(BooleanUpdatePayload.builder().state(state).build())
            .build();

        when:
        actuatorReceiver.acceptMessage(message);

        then:
        1 * patchCommand.sendPatch({ it.get("state").equals(param) }) >> { new RestResponse(new ResponseEntity<>(HttpStatus.OK)) };

        where:
        state | param
        true  | "on"
        false | "off"
    }

    def "Boolean toggle payload handled properly"() {
        given:
        def message = Message.builder()
            .senderId(new ApplianceId("100"))
            .payload(BooleanTogglePayload.TOGGLE_PAYLOAD)
            .build();

        when:
        actuatorReceiver.acceptMessage(message);

        then:
        1 * patchCommand.sendPatch({ it.get("state").equals("toggle") }) >> { new RestResponse(new ResponseEntity<>(HttpStatus.OK)) };
    }

    @Unroll
    def "Update request payload handled properly for #param == #state"() {
        given:
        def message = Message.builder()
            .senderId(new ApplianceId("100"))
            .payload(UpdateRequestPayload.UPDATE_REQUEST_PAYLOAD)
            .build();

        when:
        actuatorReceiver.acceptMessage(message);

        then:
        1 * getCommand.sendGet(BinOutState.class) >> {
            BinOutState state = new BinOutState();
            state.setState(param);
            return new RestResponse<>(new ResponseEntity<>(state, HttpStatus.OK));
        };
        1 * messagePublisher.acceptMessage({
            it.senderId.equals(actuatorId) && it.payload.getClass() == BooleanUpdatePayload && it.payload.state == state
        });

        where:
        state | param
        true  | "on"
        false | "off"
    }

    def "Response status not OK from path command throws exception"() {
        given:
        def message = Message.builder()
            .senderId(new ApplianceId("100"))
            .payload(BooleanUpdatePayload.builder().state(false).build())
            .build();

        when:
        actuatorReceiver.acceptMessage(message);

        then:
        1 * patchCommand.sendPatch(_) >> { new RestResponse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)) };
        thrown MessageProcessingException;
    }

    def "Patch call exception handled to message processing exception"() {
        given:
        def message = Message.builder()
            .senderId(new ApplianceId("100"))
            .payload(BooleanUpdatePayload.builder().state(false).build())
            .build();

        when:
        actuatorReceiver.acceptMessage(message);

        then:
        1 * patchCommand.sendPatch(_) >> { throw new RestRequestException("") };
        thrown MessageProcessingException;
    }

    def "Response status not OK from get command throws exception"() {
        given:
        def message = Message.builder()
            .senderId(new ApplianceId("100"))
            .payload(UpdateRequestPayload.UPDATE_REQUEST_PAYLOAD)
            .build();

        when:
        actuatorReceiver.acceptMessage(message);

        then:
        1 * getCommand.sendGet(_) >> { new RestResponse<>(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)); }
        thrown MessageProcessingException;
    }

    def "Get call exception handled to message processing exception"() {
        given:
        def message = Message.builder()
            .senderId(new ApplianceId("100"))
            .payload(UpdateRequestPayload.UPDATE_REQUEST_PAYLOAD)
            .build();

        when:
        actuatorReceiver.acceptMessage(message);

        then:
        1 * getCommand.sendGet(_) >> { throw new RestRequestException("") };
        thrown MessageProcessingException;
    }
}
