package org.wikiup.core.inf.ext;

import org.wikiup.core.inf.Document;

public interface Wirable<T, P> {
    public T wire(P param);

    public interface ByDocument<T> extends Wirable<T, Document> {
    }
}
