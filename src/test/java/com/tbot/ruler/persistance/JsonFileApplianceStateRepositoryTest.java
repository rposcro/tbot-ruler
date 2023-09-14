package com.tbot.ruler.persistance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.tbot.ruler.model.Measure;
import com.tbot.ruler.model.MeasureQuantity;
import com.tbot.ruler.model.OnOffState;
import com.tbot.ruler.model.RGBWColor;
import com.tbot.ruler.persistance.json.JsonFileApplianceStateRepository;
import com.tbot.ruler.persistance.model.ApplianceState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JsonFileApplianceStateRepositoryTest {

    private static Path statesFile;
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void beforeClass() throws IOException {
        statesFile = Files.createTempFile(".", "test-states.json");
        objectMapper = new ObjectMapper().registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
    }

    @Test
    @Order(1)
    public void newRepositoryIsUpAndWorking() {
        JsonFileApplianceStateRepository repository = createRepository();
        repository.initialize();

        repository.save(ApplianceState.builder().key("ble-blah").value(mockMeasure(123, 1)).valueClass(Measure.class).build());
        repository.save(ApplianceState.builder().key("ah-oh").value(mockOnOff(true)).valueClass(OnOffState.class).build());
        repository.save(ApplianceState.builder().key("ble-blah").value(mockMeasure(139, 2)).valueClass(Measure.class).build());
        repository.save(ApplianceState.builder().key("col-or").value(mockRGBW(12, 100, 50, 99)).valueClass(RGBWColor.class).build());

        assertRepoContent(repository);
    }

    @Test
    @Order(2)
    public void existingRepositoryIsOpenedAndWorking() {
        JsonFileApplianceStateRepository repository = createRepository();
        repository.initialize();

        assertRepoContent(repository);
    }

    private void assertRepoContent(JsonFileApplianceStateRepository repository) {
        assertFalse(repository.findByKey("non-exisitng").isPresent());

        ApplianceState state = repository.findByKey("ble-blah").get();
        assertEquals("ble-blah", state.getKey());
        assertEquals(Measure.class, state.getValueClass());
        assertEquals(Measure.class, state.getValue().getClass());
        Measure measure = (Measure) state.getValue();
        assertEquals(MeasureQuantity.Temperature, measure.getQuantity());
        assertEquals("C", measure.getUnit());
        assertEquals(139, measure.getValue());
        assertEquals(2, measure.getDecimals());

        state = repository.findByKey("ah-oh").get();
        assertEquals("ah-oh", state.getKey());
        assertEquals(OnOffState.class, state.getValueClass());
        assertEquals(OnOffState.class, state.getValue().getClass());
        OnOffState onOff = (OnOffState) state.getValue();
        assertTrue(onOff.isOn());

        state = repository.findByKey("col-or").get();
        assertEquals("col-or", state.getKey());
        assertEquals(RGBWColor.class, state.getValueClass());
        assertEquals(RGBWColor.class, state.getValue().getClass());
        RGBWColor color = (RGBWColor) state.getValue();
        assertEquals(12, color.getRed());
        assertEquals(100, color.getGreen());
        assertEquals(50, color.getBlue());
        assertEquals(99, color.getWhite());
    }

    private JsonFileApplianceStateRepository createRepository() {
        return new JsonFileApplianceStateRepository(statesFile.toString(), objectMapper);
    }

    private RGBWColor mockRGBW(int red, int green, int blue, int white) {
        return RGBWColor.of(red, green, blue, white);
    }

    private OnOffState mockOnOff(boolean state) {
        return OnOffState.of(state);
    }

    private Measure mockMeasure(long value, int precision) {
        return Measure.builder()
                .quantity(MeasureQuantity.Temperature)
                .unit("C")
                .value(value)
                .decimals((short) precision)
                .build();
    }
}
