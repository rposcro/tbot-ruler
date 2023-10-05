package com.tbot.ruler.configuration;

import com.tbot.ruler.persistance.converters.JsonReadingConverter;
import com.tbot.ruler.persistance.converters.JsonWritingConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class JdbcConfiguration extends AbstractJdbcConfiguration {

    @Autowired
    private JsonReadingConverter jsonReadingConverter;

    @Autowired
    private JsonWritingConverter jsonWritingConverter;

    @Override
    protected List<?> userConverters() {
        return Arrays.asList(jsonWritingConverter, jsonReadingConverter);
    }
}
