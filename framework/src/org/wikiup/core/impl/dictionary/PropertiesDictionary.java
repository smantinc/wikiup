package org.wikiup.core.impl.dictionary;

import org.wikiup.core.inf.Dictionary;

import java.util.Properties;


public class PropertiesDictionary implements Dictionary<String> {
    private Properties properties;

    public PropertiesDictionary(Properties properties) {
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
