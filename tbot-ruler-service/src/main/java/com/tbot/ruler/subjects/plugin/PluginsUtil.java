package com.tbot.ruler.subjects.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.util.PackageScanner;

import java.io.IOException;
import java.util.Set;

public class PluginsUtil {

    public static <T> T parseConfiguration(JsonNode jsonNode, Class<T> clazz) {
        try {
            if (jsonNode == null) {
                return clazz.getConstructor().newInstance();
            }
            return new ObjectMapper().readerFor(clazz).readValue(jsonNode);
        } catch(IOException e) {
            throw new PluginException("Could not parse configuration for class " + clazz, e);
        } catch(NoSuchMethodException e) {
            throw new PluginException("Could not create configuration instance, no default constructor!", e);
        } catch(Exception e) {
            throw new PluginException("Could not create configuration instance due to an error!", e);
        }
    }

    public static <T> Set<T> instantiateActuatorsBuilders(Class<T> builderSuperClass, String... packages) {
        PackageScanner packageScanner = new PackageScanner();
        Set<Class<? extends T>> buildersClasses = packageScanner.findAllClassesOfType(builderSuperClass, packages);
        Set<T> builders = packageScanner.instantiateAll(buildersClasses);
        return builders;
    }

    public static <T> Set<T> instantiateActuatorsBuilders(Class<T> builderSuperClass, String basePackage, Object... arguments) {
        PackageScanner packageScanner = new PackageScanner();
        Set<Class<? extends T>> buildersClasses = packageScanner.findAllClassesOfType(builderSuperClass, basePackage);
        Set<T> builders = packageScanner.instantiateAll(buildersClasses, arguments);
        return builders;
    }
}
