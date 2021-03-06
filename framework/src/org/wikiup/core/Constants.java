package org.wikiup.core;

import org.wikiup.core.impl.cl.ClassDictionaryImpl;
import org.wikiup.core.impl.factory.FactoryImpl;

public class Constants {
    public static class Attributes {
        public static final String CLASS = "class";
        public static final String OVERRIDE = "override";
        public static final String INTERFACE = "interface";
        public static final String TYPE = "type";
        public static final String NAME = "name";
        public static final String BEAN = "bean";
        public static final String ID = "id";
        public static final String VALUE = "value";
        public static final String PROPERTY = "property";
        public static final String TRANSLATOR = "translator";
        public static final String STYLE = "style";
        public static final String ENTITY_PATH = "entity-path";
        public static final String ENTITY_NAME = "entity-name";
        public static final String PROPERTY_NAME = "property-name";
    }
    
    public static class Elements {
        public static final String ACTION = "action";
        public static final String PROCESSOR = "processor";
        public static final String CONTEXT = "context";
        public static final String HANDLER = "handler";
        public static final String HEADERS = "headers";
        public static final String CLASS = "class";
        public static final String ROOT = "root";
    }

    public static class Factories {
        public static final FactoryImpl<Object> BY_CLASS_NAME = new FactoryImpl<Object>(new ClassDictionaryImpl());
    }
    
    public static class Configure {
        public static final boolean SCRATCHPAD = true;
    }
}
