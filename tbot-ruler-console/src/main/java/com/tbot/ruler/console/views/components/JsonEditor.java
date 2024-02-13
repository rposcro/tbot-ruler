package com.tbot.ruler.console.views.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.tbot.ruler.console.exceptions.ViewRenderException;
import com.tbot.ruler.console.utils.FormUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import elemental.json.JsonValue;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@JsModule("./components/json-editor/jsoneditor.min.js")
@JavaScript(value = "./components/json-editor/jsoneditor.actions.js")
@StyleSheet("json-editor/jsoneditor.min.css")
public class JsonEditor extends Div {

    private final String domElementId;
    private final String label;

    public JsonEditor(String label) {
        this.domElementId = "_jsonEditorDiv_" + System.currentTimeMillis();
        this.label = label;

        this.setWidthFull();
        this.setHeightFull();
        this.setId(domElementId);
        executeScript("saySomething()");
        executeScript("jsonEditorInit($0)", domElementId);
    }

    public void setJson(JsonNode jsonNode) {
        executeScript(String.format("jsonEditorSet(%s)", FormUtils.asString(jsonNode)));
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
