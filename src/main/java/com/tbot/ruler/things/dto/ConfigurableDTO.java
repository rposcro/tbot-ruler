package com.tbot.ruler.things.dto;

import com.tbot.ruler.util.ParseUtil;
import lombok.Data;

import java.util.Collections;
import java.util.Map;

@Data
public abstract class ConfigurableDTO {

    private Map<String, String> config = Collections.emptyMap();;

    public String getStringParameter(String paramName) {
        return config.get(paramName);
    }

    public String getStringParameter(String paramName, String defaultValue) {
        return config.getOrDefault(paramName, defaultValue);
    }

    public int getIntParameter(String paramName) {
        return ParseUtil.parseInt(config.get(paramName));
    }

    public int getIntParameter(String paramName, int defaultValue) {
        String value = config.get(paramName);
        return value != null ? ParseUtil.parseInt(value) : defaultValue;
    }
}
