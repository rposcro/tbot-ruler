package com.tbot.ruler.service.plugins;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.exceptions.PluginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PluginConfigurationDeserializer {

    private final static Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");
    private final static String SECRET_PROPERTY_PREFIX = "ruler.secrets.plugins.";

    @Autowired
    private Environment environment;

    @Autowired
    private ObjectMapper objectMapper;

    public <T> T parseConfiguration(JsonNode jsonNode, Class<T> clazz) {
        try {
            if (jsonNode == null) {
                return clazz.getConstructor().newInstance();
            }
            String configAsString = objectMapper.writeValueAsString(jsonNode);
            configAsString = resolveSecrets(configAsString);
            T configuration = objectMapper.readerFor(clazz).readValue(configAsString);
            return configuration;
        } catch(IOException e) {
            throw new PluginException("Could not parse configuration for class " + clazz, e);
        } catch(NoSuchMethodException e) {
            throw new PluginException("Could not create configuration instance, no default constructor!", e);
        } catch(Exception e) {
            throw new PluginException("Could not create configuration instance due to an error!", e);
        }
    }

    private String resolveSecrets(String rawString) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(rawString);
        return matcher.replaceAll(this::resolveSecret);
    }

    private String resolveSecret(MatchResult matchResult) {
        String propertyName = SECRET_PROPERTY_PREFIX + matchResult.group(1);
        String secret = environment.getProperty(propertyName);

        if (secret == null) {
            throw new IllegalArgumentException("Secret property '" + propertyName + "' not found!");
        }

        return secret;
    }
}
