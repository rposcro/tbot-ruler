package com.tbot.ruler.console.views.components;

import com.tbot.ruler.console.utils.BeanReader;

import java.util.HashMap;
import java.util.Map;

public class EntityGridFilter<T> {

    private final String[] properties;
    private final BeanReader beanReader;
    private final Map<String, String> filterValues;

    public EntityGridFilter(BeanReader beanReader, String... properties) {
        this.properties = properties;
        this.beanReader = beanReader;
        this.filterValues = new HashMap<>();
    }

    public void setFilterValue(String property, String value) {
        filterValues.put(property, value);
    }

    public boolean test(T bean) {
        for (String property: properties) {
            String value = beanReader.propertyAsString(bean, property);
            if (!matches(value, filterValues.get(property))) {
                return false;
            }
        }

        return true;
    }

    private boolean matches(String value, String searchTerm) {
        return value == null || searchTerm == null || searchTerm.isEmpty()
                || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
