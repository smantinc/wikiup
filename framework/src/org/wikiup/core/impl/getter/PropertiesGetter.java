package org.wikiup.core.impl.getter;

import org.wikiup.core.inf.Getter;

import java.util.Properties;


public class PropertiesGetter implements Getter<String> {
    private Properties properties;

    public PropertiesGetter(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String get(String name) {
        return properties.getProperty(name);
    }
}
