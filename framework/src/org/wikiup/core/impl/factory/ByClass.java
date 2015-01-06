package org.wikiup.core.impl.factory;

import org.wikiup.core.Constants;
import org.wikiup.core.inf.Builder;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;

import java.util.Map;

public class ByClass<T> implements Builder<T> {
    private Map<String, Builder<T>> builders;
    private String attributeName;

    public ByClass(Map<String, Builder<T>> builders) {
        this(builders, Constants.Attributes.CLASS);
    }

    public ByClass(Map<String, Builder<T>> builders, String attributeName) {
        this.builders = builders;
        this.attributeName = attributeName;
    }

    @Override
    public T build(Document desc) {
        String clazz = Documents.getAttributeValue(desc, attributeName, null);
        Builder<T> builder = builders.get(clazz);
        return builder != null ? builder.build(desc) : null;
    }
}
