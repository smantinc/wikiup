package org.wikiup.core.impl.document;


import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.element.Context2Element;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.Setter;
import org.wikiup.core.inf.ext.Context;

public class Context2Document extends Context2Element implements Document {
    public Context2Document(Getter<?> getter, Setter<?> setter, Iterable<String> iterable) {
        super(getter, setter, iterable);
    }

    public Context2Document(Context<?, ?> context, Iterable<String> iterable) {
        super(context, iterable);
    }

    public Context2Document(Setter<?> setter, Iterable<String> iterable) {
        super(setter, iterable);
    }

    public Context2Document(Getter<?> getter, Iterable<String> iterable) {
        super(getter, iterable);
    }

    public Document getChild(String name) {
        return null;
    }

    public Document addChild(String name) {
        return Null.getInstance();
    }

    public Iterable<Document> getChildren(String name) {
        return Null.getInstance();
    }

    public Iterable<Document> getChildren() {
        return Null.getInstance().iter();
    }

    public void removeNode(Document child) {
    }

    public Document getParentNode() {
        return null;
    }
}
