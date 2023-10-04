package com.tbot.ruler.persistance.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.exceptions.CriticalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class JsonReadingConverter implements Converter<String, JsonNode> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public JsonNode convert(String jsonString) {
        try {
            return objectMapper.readTree(jsonString);
        } catch(JsonProcessingException e) {
            throw new CriticalException("Cannot convert string to json: " + jsonString, e);
        }
    }
}
