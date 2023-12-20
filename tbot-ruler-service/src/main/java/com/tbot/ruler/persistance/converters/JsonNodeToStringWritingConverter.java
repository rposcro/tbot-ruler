package com.tbot.ruler.persistance.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.exceptions.CriticalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class JsonNodeToStringWritingConverter implements Converter<JsonNode, String> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String convert(JsonNode jsonNode) {
        try {
            return objectMapper.writeValueAsString(jsonNode);
        } catch(JsonProcessingException e) {
            throw new CriticalException("Cannot convert json to string!", e);
        }
    }
}
