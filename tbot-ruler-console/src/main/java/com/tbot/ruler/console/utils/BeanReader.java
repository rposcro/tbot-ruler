package com.tbot.ruler.console.utils;

import com.tbot.ruler.console.exceptions.ViewRenderException;
import com.vaadin.flow.internal.BeanUtil;
import lombok.extern.slf4j.Slf4j;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class BeanReader {

    private final String[] knownProperties;
    private final Map<String, PropertyDescriptor> descriptorsMap;

    public BeanReader(Class<?> beanType, String... properties) {
        descriptorsMap = extractBeanProperties(beanType, properties).stream()
                .collect(Collectors.toMap(
                        PropertyDescriptor::getName,
                        Function.identity(),
                        (k, v) -> v,
                        LinkedHashMap::new));
        knownProperties = descriptorsMap.keySet().toArray(new String[0]);
    }

    public String getDisplayName(String property) {
        PropertyDescriptor descriptor = descriptorsMap.get(property);
        return descriptor != null ? descriptor.getDisplayName() : null;
    }

    public String[] getKnownProperties() {
        return this.knownProperties;
    }

    public String propertyAsString(Object bean, String propertyName) {
        try {
            PropertyDescriptor descriptor = descriptorsMap.get(propertyName);
            if (descriptor != null) {
                Object value = descriptor.getReadMethod().invoke(bean);
                return value == null ? null : value.toString();
            }
        } catch(Exception e) {
            log.warn("Exception while reading bean property value", e);
        }
        return null;
    }

    private List<PropertyDescriptor> extractBeanProperties(Class<?> beanType) {
        try {
            List<PropertyDescriptor> descriptors = BeanUtil.getBeanPropertyDescriptors(beanType);
            return descriptors.stream()
                    .filter(this::considerProperty)
                    .peek(this::enhanceDescriptor)
                    .collect(Collectors.toList());
        } catch(IntrospectionException e) {
            throw new ViewRenderException("Failed to set up Entity Properties Panel!", e);
        }
    }

    private List<PropertyDescriptor> extractBeanProperties(Class<?> beanType, String[] properties) {
        if (properties == null || properties.length == 0) {
            return extractBeanProperties(beanType);
        }

        try {
            List<PropertyDescriptor> descriptors = new ArrayList<>(properties.length);
            for (String property: properties) {
                PropertyDescriptor descriptor = BeanUtil.getPropertyDescriptor(beanType, property);
                descriptors.add(descriptor);
            }
            return descriptors.stream()
                    .filter(this::considerProperty)
                    .peek(this::enhanceDescriptor)
                    .collect(Collectors.toList());
        } catch(IntrospectionException e) {
            throw new ViewRenderException("Failed to set up Entity Properties Panel!", e);
        }
    }

    private boolean considerProperty(PropertyDescriptor descriptor) {
        return descriptor.getReadMethod() != null
                && descriptor.getReadMethod().getParameterCount() == 0
                && !"class".equals(descriptor.getName());
    }

    private void enhanceDescriptor(PropertyDescriptor descriptor) {
        StringBuffer displayName = new StringBuffer();
        char[] characters = descriptor.getName().toCharArray();
        int wordIdx = 0;
        boolean lastUpper = false;

        for (int i = 0; i < characters.length; i++) {
            if (wordIdx == 0) {
                displayName.append(Character.toUpperCase(characters[i]));
                wordIdx++;
            } else if (lastUpper) {
                displayName.append(characters[i]);
                wordIdx++;
            } else if (Character.isLowerCase(characters[i])) {
                displayName.append(characters[i]);
                wordIdx++;
            } else {
                displayName.append(' ').append(characters[i]);
                wordIdx = 1;
            }

            lastUpper = Character.isUpperCase(characters[i]);
        }

        descriptor.setDisplayName(displayName.toString());
    }

}
