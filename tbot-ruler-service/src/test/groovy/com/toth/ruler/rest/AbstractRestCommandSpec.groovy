package com.toth.ruler.rest

import com.tbot.ruler.exceptions.RestRequestException
import com.tbot.ruler.rest.RestGetCommand
import com.tbot.ruler.rest.RestResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClientException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.Supplier

@Unroll
class AbstractRestCommandSpec extends Specification {

    @Shared def maxRetries = 5;

    def restCommand;

    def setup() {
        restCommand = RestGetCommand.builder()
            .host("dummy.host")
            .path("dummy/path")
            .port("987")
            .retryCount(maxRetries)
            .build();
    }

    def "get succeeds after #tryCount tries"() {
        given:
        def requestExecutor = requestExecutor(tryCount);

        when:
        RestResponse response = restCommand.executeRequest(requestExecutor);

        then:
        response.getBody() == "SUCCESS";
        requestExecutor.callsNumber == tryCount;

        where:
        tryCount    | _
        1           | _
        2           | _
        maxRetries  | _
    }

    def "get fails when retryCount exceeded"() {
        given:
        def requestExecutor = requestExecutor(maxRetries + 1);

        when:
        restCommand.executeRequest(requestExecutor);

        then:
        thrown RestRequestException;
    }

    def requestExecutor(int successAfter) {
        return new Supplier() {
            int callsNumber = 0;
            @Override
            Object get() {
                if (++callsNumber >= successAfter) {
                    return new RestResponse(ResponseEntity.ok().body("SUCCESS"));
                };
                throw new RestClientException("Faked request timeout");
            }
        }
    }
}
