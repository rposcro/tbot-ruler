package com.tbot.ruler.console.views;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.console.exceptions.ViewRenderException;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.internal.BeanUtil;
import lombok.Builder;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityPropertiesPanel<T> extends VerticalLayout {

    private final ObjectMapper objectMapper;
    private final List<PropertyDescriptor> descriptors;
    private final Map<String, Div> labelsMap = new HashMap<>();

    private final Button btnEdit = new Button("Edit");
    private final Button btnDelete = new Button("Delete");

    @Builder
    public EntityPropertiesPanel(Class<?> beanType, Runnable editHandler, Runnable deleteHandler, String... properties) {
        this.descriptors = extractBeanProperties(beanType, properties);
        this.objectMapper = new ObjectMapper();
        setUpPanel();
        setUpButtons(editHandler, deleteHandler);
    }

    public void applyToEntity(T entity) {
        if (entity != null) {
            for (PropertyDescriptor descriptor : descriptors) {
                HtmlContainer value = renderValue(descriptor, entity);
                Div valueDiv = labelsMap.get(descriptor.getName());
                valueDiv.removeAll();
                valueDiv.add(value);
            }
            setVisible(true);
         } else {
            setVisible(false);
            labelsMap.values().stream().forEach(span -> span.setText(""));
        }
    }

    private void setUpButtons(Runnable editHandler, Runnable deleteHandler) {
        btnEdit.setEnabled(editHandler != null);
        btnEdit.getStyle().set("margin-left", "auto");
        if (editHandler != null) {
            btnEdit.addClickListener(event -> editHandler.run());
        }

        btnDelete.setEnabled(deleteHandler != null);
        btnDelete.getStyle().set("margin-right", "auto");
        if (deleteHandler != null) {
            btnDelete.addClickListener(event -> deleteHandler.run());
        }

        HorizontalLayout actionsPane = new HorizontalLayout(btnEdit, btnDelete);
        add(actionsPane);
    }

    private void setUpPanel() {
        setWidthFull();
        setVisible(false);

        for (PropertyDescriptor descriptor: descriptors) {
            Div propertyDiv = new Div();
            propertyDiv.setWidthFull();

            Div labelDiv = new Div(new Span(descriptor.getDisplayName()));
            labelDiv.setClassName("property-label");
            labelDiv.setWidthFull();

            Div valueDiv = new Div();
            valueDiv.setClassName("property-value");
            valueDiv.setWidthFull();

            propertyDiv.add(labelDiv, valueDiv);
            add(propertyDiv);
            labelsMap.put(descriptor.getName(), valueDiv);
        }
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
        if (properties == null && properties.length == 0) {
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

    private HtmlContainer renderValue(PropertyDescriptor descriptor, Object bean) {
        try {
            Object value = descriptor.getReadMethod().invoke(bean);

            if (value instanceof JsonNode) {
                return new Pre(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value));
            } else {
                return new Span(value == null ? "" : value.toString());
            }
        } catch(Exception e) {
            return new Span("<Error> " + e.getMessage());
        }
    }
}
