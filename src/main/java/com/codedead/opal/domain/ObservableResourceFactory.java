package com.codedead.opal.domain;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ResourceBundle;

public final class ObservableResourceFactory {

    private final ObjectProperty<ResourceBundle> resources;

    /**
     * Initialize a new ObservableResourceFactory
     */
    public ObservableResourceFactory() {
        resources = new SimpleObjectProperty<>();
    }

    /**
     * Get the resources property
     *
     * @return The resources property
     */
    public ObjectProperty<ResourceBundle> resourcesProperty() {
        return resources;
    }

    /**
     * Get the resources
     *
     * @return The resources
     */
    public ResourceBundle getResources() {
        return resourcesProperty().get();
    }

    /**
     * Set the resources
     *
     * @param resources The resources
     */
    public void setResources(final ResourceBundle resources) {
        resourcesProperty().set(resources);
    }

    /**
     * Get a string binding for the given key
     *
     * @param key The key
     * @return The {@link StringBinding} for the given key
     */
    public StringBinding getStringBinding(final String key) {
        return new StringBinding() {
            {
                bind(resourcesProperty());
            }

            @Override
            public String computeValue() {
                return getResources().getString(key);
            }
        };
    }
}
