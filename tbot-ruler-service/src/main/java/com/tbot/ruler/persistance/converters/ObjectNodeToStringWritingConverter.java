package com.tbot.ruler.persistance.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tbot.ruler.exceptions.CriticalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class ObjectNodeToStringWritingConverter implements Converter<ObjectNode, String> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String convert(ObjectNode jsonNode) {
        try {
            return objectMapper.writeValueAsString(jsonNode);
        } catch(JsonProcessingException e) {
            throw new CriticalException("Cannot convert json to string!", e);
        }
    }
}
