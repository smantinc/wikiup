package org.wikiup.core;

import org.wikiup.core.impl.cl.ClassDictionaryImpl;
import org.wikiup.core.impl.factory.FactoryByName;

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
    }
    
    public static class Elements {
        public static final String ACTION = "action";
        public static final String PROCESSOR = "processor";
        public static final String CONTEXT = "context";
    }

    public static class Factories {
        public static final FactoryByName<Object> BY_CLASS_NAME = new FactoryByName<Object>(new ClassDictionaryImpl());
    }
    
    public static class Configure {
        public static final boolean SCRATCHPAD = true;
    }
}
