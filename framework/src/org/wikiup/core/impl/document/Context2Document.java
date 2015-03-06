package org.wikiup.core.impl.document;


import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.element.Context2Element;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.ext.Context;

public class Context2Document extends Context2Element implements Document {
    public Context2Document(Dictionary<?> dictionary, Dictionary.Mutable<?> mutable, Iterable<String> iterable) {
        super(dictionary, mutable, iterable);
    }

    public Context2Document(Context<?, ?> context, Iterable<String> iterable) {
        super(context, iterable);
    }

    public Context2Document(Dictionary.Mutable<?> mutable, Iterable<String> iterable) {
        super(mutable, iterable);
    }

    public Context2Document(Dictionary<?> dictionary, Iterable<String> iterable) {
        super(dictionary, iterable);
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
