package com.tbot.ruler.things.configuration

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.tbot.ruler.configuration.BindingsConfiguration
import com.tbot.ruler.configuration.DTOConfiguration
import com.tbot.ruler.util.FileUtil
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

@Ignore
class BindingsConfigurationSpec extends Specification {

    def String bindingsJsonString = this.getClass().getResource( '/bindings/test-bindings-lights.json' ).text;

    @Shared
    def ObjectMapper objectMapper;

    def setupSpec() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
    }

    def "emitters to appliances map"() {
        given:
        BindingsConfiguration configuration = new BindingsConfiguration();
        DTOConfiguration.WrapperDTO dto = objectMapper.readValue(bindingsJsonString, new TypeReference<DTOConfiguration.WrapperDTO>() {});
        List<?> bindings = configuration.appliancesBindings(dto.bindings);

        when:
        Map<String, Set<String>> pivots = configuration.emittersToAppliancesMap(bindings);

        then:
        pivots != null;
        pivots.size() == 3;

        pivots.get("emitter-appliance-one-emitter-1").size() == 1;
        pivots.get("emitter-appliance-one-emitter-1").contains("appliance-one-emitter");

        pivots.get("emitter-1").size() == 3;
        pivots.get("emitter-1").contains("appliance-two-emitters");
        pivots.get("emitter-1").contains("appliance-one-each");
        pivots.get("emitter-1").contains("appliance-multiple-each");

        pivots.get("emitter-2").size() == 2;
        pivots.get("emitter-2").contains("appliance-multiple-each");
        pivots.get("emitter-2").contains("appliance-two-emitters");
    }
/*
    def "collectors to appliances map"() {
        then:
        pivots.get("collector-appliance-one-collector-1").size() == 1;
        pivots.get("collector-appliance-one-collector-1").contains("appliance-one-collector");

        pivots.get("collector-1").size() == 3;
        pivots.get("collector-1").contains("appliance-multiple-each");
        pivots.get("collector-1").contains("appliance-one-each");
        pivots.get("collector-1").contains("appliance-two-collectors");

        pivots.get("collector-2").size() == 2;
        pivots.get("collector-2").contains("appliance-multiple-each");
        pivots.get("collector-2").contains("appliance-two-collectors");

        pivots.get("collector-3").size() == 1;
        pivots.get("collector-3").contains("appliance-multiple-each");
    }
*/
    def "deserialize appliances bindings"() {
        given:
        BindingsConfiguration configuration = new BindingsConfiguration();
        configuration.configPath = "target/test-classes";
        configuration.fileUtil = new FileUtil();

        when:
        List<?> bindings = configuration.appliancesBindings();
        sortBindingsByApplianceId(bindings);

        then:
        bindings != null;
        bindings.size() == 7;
        bindings.get(0).applianceId == "appliance-multiple-each";
        bindings.get(0).emitterIds.size() == 2;
        bindings.get(0).emitterIds.get(0) == "emitter-1";
        bindings.get(0).emitterIds.get(1) == "emitter-2";
        bindings.get(0).collectorIds.size() == 3;
        bindings.get(0).collectorIds.get(0) == "collector-1";
        bindings.get(0).collectorIds.get(1) == "collector-2";
        bindings.get(0).collectorIds.get(2) == "collector-3";

        bindings.get(1).applianceId == "appliance-one-collector";
        bindings.get(1).emitterIds.isEmpty() == true;
        bindings.get(1).collectorIds.size() == 1;
        bindings.get(1).collectorIds.get(0) == "collector-appliance-one-collector-1";

        bindings.get(4).applianceId == "appliance-simple";
        bindings.get(4).emitterIds.size() == 1;
        bindings.get(4).emitterIds.get(0) == "emitter-appliance-simple";
        bindings.get(4).collectorIds.size() == 1;
        bindings.get(4).collectorIds.get(0) == "collector-appliance-simple";
    }

    def sortBindingsByApplianceId(List<?> bindings) {
        Comparator<?> comparator = new Comparator<?>() {
            @Override
            public int compare(Object b1, Object b2) {
                return b1.getApplianceId().compareTo(b2.getApplianceId());
            }
        };
        Collections.sort(bindings, comparator);
    }
}
