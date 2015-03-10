package org.wikiup.servlet.inf;

import org.wikiup.core.inf.Dictionary;

public interface ProcessorContext extends Dictionary<Object> {
    public interface ByParameters {
        public Object get(String name, Dictionary<?> params);
    }
}
