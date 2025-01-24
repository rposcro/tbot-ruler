package com.tbot.ruler.console.views.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.tbot.ruler.console.exceptions.ViewRenderException;
import com.tbot.ruler.console.utils.FormUtils;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import elemental.json.JsonValue;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@Tag("tbot-json-editor")
@JsModule("./tbot-json-editor/tbot-json-editor.ts")
@CssImport("./tbot-json-editor/tbot-json-editor.scss")
public class JsonEditor extends LitTemplate implements HasSize, HasComponents, HasStyle {

    private final String domElementId;
    @Getter
    private final String label;

    public JsonEditor(String title) {
        this.domElementId = "_jsonEditorDiv_" + System.currentTimeMillis();
        this.label = "Testing";

        this.setId(domElementId);
        this.setWidthFull();
        this.setHeightFull();
    }

    public void setJson(JsonNode jsonNode) {
//        executeScript(String.format("setText(%s)", FormUtils.asString(jsonNode)));
    }

    public String getJsonString() {
        try {
            JsonValue json = getElement().executeJs("jsonEditorGet()")
                .toCompletableFuture()
                .get(2, TimeUnit.SECONDS);
            return json.asString();
        } catch(Exception e) {
            log.error("Failed to read json from UI", e);
            throw new ViewRenderException("Failed to read json from UI", e);
        }
    }

    public JsonNode getJson() {
        return FormUtils.asJsonNode(getJsonString());
    }

    private void executeScript(String script, String... parameters) {
        UI.getCurrent().getPage().executeJs(script, parameters);
    }
}
