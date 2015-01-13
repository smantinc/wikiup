package org.wikiup.core.inf.ext;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.Wrapper;

public interface Wirable<T, P> extends Wrapper<T> {
    public T wire(P param);
    
    public interface ByDocument<T> extends Wirable<T, Document> {
    }
}
