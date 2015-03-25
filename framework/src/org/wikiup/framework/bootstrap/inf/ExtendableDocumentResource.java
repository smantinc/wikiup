package org.wikiup.framework.bootstrap.inf;

import org.wikiup.core.inf.Document;

import java.util.List;
import java.util.Map;

public interface ExtendableDocumentResource<E> {
    public List<E> loadResources(Document doc);

    public String getSuperResourceName(E doc);

    public String getResourceName(E doc);

    public void extend(E thiz, E sup);

    public void finish(Map<String, E> hierary, List<E> resources);
}
