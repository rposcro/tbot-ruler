package com.tbot.ruler.console.views.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.tbot.ruler.console.utils.FormUtils;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Synchronize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag("tbot-json-editor")
@JsModule("./tbot-json-editor/tbot-json-editor.ts")
@CssImport("./tbot-json-editor/tbot-json-editor.scss")
public class JsonEditor extends LitTemplate implements HasSize, HasComponents, HasStyle {

    @Getter
    private final String label;

    public JsonEditor(String label, JsonNode initialJson) {
        this.label = label;

        this.setId("_jsonEditorDiv_" + System.currentTimeMillis());
        this.setWidthFull();
        this.setHeightFull();

        getElement().setProperty("currentJsonBody", FormUtils.asString(initialJson));
    }

    @Synchronize(value = "json-updated", property = "currentJsonBody")
    public String getJsonString() {
        return getElement().getProperty("currentJsonBody");
    }

    public JsonNode getJson() {
        return FormUtils.asJsonNode(getJsonString());
    }

    public void setJson(JsonNode json) {
        getElement().callJsFunction("setJsonBody", FormUtils.asString(json));
    }
}
