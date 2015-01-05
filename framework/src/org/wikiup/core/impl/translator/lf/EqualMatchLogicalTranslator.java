package org.wikiup.core.impl.translator.lf;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ext.LogicalTranslator;
import org.wikiup.core.util.Documents;

public class EqualMatchLogicalTranslator<E> implements LogicalTranslator<E>, DocumentAware {
    private Object value;

    public EqualMatchLogicalTranslator() {
    }

    public EqualMatchLogicalTranslator(Object obj) {
        value = obj;
    }

    public Boolean translate(E obj) {
        return value != null ? value.equals(obj) : obj == null;
    }

    public void aware(Document desc) {
        value = Documents.getDocumentValue(desc, "value", null);
    }
}
