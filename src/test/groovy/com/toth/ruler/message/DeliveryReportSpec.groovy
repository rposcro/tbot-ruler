package com.toth.ruler.message

import com.tbot.ruler.broker.model.MessagePublicationReport
import com.tbot.ruler.broker.model.Message
import com.tbot.ruler.broker.model.payloads.BooleanTogglePayload

import spock.lang.Specification
import spock.lang.Unroll

class DeliveryReportSpec extends Specification {

    @Unroll
    def "reports: #testName"() {
        given:
        MessagePublicationReport.DeliveryReportBuilder reportBuilder = MessagePublicationReport.builder()
            .originalMessage(Message.builder()
                .senderId(new EmitterId("1234"))
                .payload(BooleanTogglePayload.TOGGLE_PAYLOAD)
                .build())
        ;
        failedItems.stream().forEach({ item -> reportBuilder.failedReceiver(new ItemId("" + item)) });
        successfulItems.stream().forEach({ item -> reportBuilder.successfulReceiver(new ItemId("" + item)) });

        when:
        MessagePublicationReport report = reportBuilder.build();

        then:
        report.noReceiversFound() == noReceivers;
        report.publicationSuccessful() == success
        report.publicationFailed() == failed;
        report.publicationPartiallyFailed() == partFailed;

        where:
        testName | failedItems | successfulItems | noReceivers | success | failed | partFailed
        "no receivers found" | [] | [] | true | false | false | false
        "successful delivery" | [] | [1, 3] | false | true | false | false
        "failed delivery" | [3] | [] | false | false | true | false
        "partially failed delivery" | [3, 56] | [99] | false | false | false | true
    }
}
