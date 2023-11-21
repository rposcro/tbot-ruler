package com.tbot.ruler.console.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.tbot.ruler.console.exceptions.ViewRenderException;

public class FormUtils {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static String asString(JsonNode node) {
        try {
            return node == null || node.isNull() ? "" : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch(JsonProcessingException e) {
            throw new ViewRenderException("Could not convert node to String!", e);
        }
    }

    public static JsonNode asJsonNode(String content) {
        try {
            return content == null || content.trim().isEmpty() ? NullNode.getInstance() : objectMapper.readTree(content);
        } catch(JsonProcessingException e) {
            throw new ViewRenderException("Could not convert to node!", e);
        }
    }

    public static String orEmpty(String value) {
        return value == null ? "" : value;
    }
}
