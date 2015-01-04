package org.wikiup.core.impl.translator.lf;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ext.LogicalFilter;
import org.wikiup.core.util.Documents;

public class EqualMatchLogicalFilter<E> implements LogicalFilter<E>, DocumentAware {
    private Object value;

    public EqualMatchLogicalFilter() {
    }

    public EqualMatchLogicalFilter(Object obj) {
        value = obj;
    }

    public Boolean translate(E obj) {
        return value != null ? value.equals(obj) : obj == null;
    }

    public void aware(Document desc) {
        value = Documents.getDocumentValue(desc, "value", null);
    }
}
